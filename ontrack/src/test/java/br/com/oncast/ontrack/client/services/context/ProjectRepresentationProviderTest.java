package br.com.oncast.ontrack.client.services.context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationListener;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListResponse;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ProjectRepresentationProviderTest {

	@Mock
	private DispatchService dispatch;

	@Mock
	private ServerPushClientService serverPush;

	@Mock
	private AuthenticationService auth;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void updateAvaliableProjectsOnInitialization() throws Exception {
		new ProjectRepresentationProviderImpl(dispatch, serverPush, auth);
		assureProjectsWereRequested();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void nothingHappensIfSearchForAvaliableProjectsFails() throws Exception {
		AsyncDispatchCallbackMock.callOnFailureWith(new RuntimeException()).when(dispatch)
				.dispatch(Mockito.any(ProjectListRequest.class), Mockito.any(DispatchCallback.class));

		new ProjectRepresentationProviderImpl(dispatch, serverPush, auth);
		assureProjectsWereRequested();
	}

	@Test
	public void updateAvaliableProjectsWhenAUserLogsIn() throws Exception {
		new ProjectRepresentationProviderImpl(dispatch, serverPush, auth);

		final ArgumentCaptor<UserAuthenticationListener> captor = ArgumentCaptor.forClass(UserAuthenticationListener.class);
		verify(auth).registerUserAuthenticationListener(captor.capture());
		captor.getValue().onUserLoggedIn();

		assureProjectsWereRequested(2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void cleanAvaliableProjectsWhenAUserLogsOut() throws Exception {
		final List<ProjectRepresentation> projectList = new ArrayList<ProjectRepresentation>();

		final List<ProjectRepresentation> projects = new ArrayList<ProjectRepresentation>();
		projects.add(ProjectTestUtils.createProjectRepresentation());

		AsyncDispatchCallbackMock.callOnRequestCompletitionWith(projects).when(dispatch)
				.dispatch(Mockito.any(ProjectListRequest.class), Mockito.any(DispatchCallback.class));

		final ProjectRepresentationProviderImpl provider = new ProjectRepresentationProviderImpl(dispatch, serverPush, auth);
		provider.registerProjectListChangeListener(new ProjectListChangeListener() {

			@Override
			public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {
				projectList.clear();
				projectList.addAll(projectRepresentations);
			}
		});

		assertEquals(1, projectList.size());

		final ArgumentCaptor<UserAuthenticationListener> captor = ArgumentCaptor.forClass(UserAuthenticationListener.class);
		verify(auth).registerUserAuthenticationListener(captor.capture());

		captor.getValue().onUserLoggedOut();

		assertEquals(0, projectList.size());
	}

	private void assureProjectsWereRequested() {
		assureProjectsWereRequested(1);
	}

	@SuppressWarnings("unchecked")
	private void assureProjectsWereRequested(final int expectedTimes) {
		verify(dispatch, times(expectedTimes)).dispatch(Mockito.any(ProjectListRequest.class), Mockito.any(DispatchCallback.class));
	}

	@SuppressWarnings("rawtypes")
	private static class AsyncDispatchCallbackMock {
		private static Stubber callOnRequestCompletitionWith(final List<ProjectRepresentation> projects) {
			return Mockito.doAnswer(new Answer<List<ProjectRepresentation>>() {
				@SuppressWarnings("unchecked")
				@Override
				public List<ProjectRepresentation> answer(final InvocationOnMock invocationOnMock) throws Throwable {
					final Object[] args = invocationOnMock.getArguments();
					((DispatchCallback) args[args.length - 1]).onSuccess(new ProjectListResponse(projects));
					return null;
				}
			});
		}

		private static Stubber callOnFailureWith(final Throwable data) {
			return Mockito.doAnswer(new Answer<Throwable>() {
				@Override
				public Throwable answer(final InvocationOnMock invocationOnMock) throws Throwable {
					final Object[] args = invocationOnMock.getArguments();
					((DispatchCallback) args[args.length - 1]).onTreatedFailure(data);
					return null;
				}
			});
		}
	}
}
