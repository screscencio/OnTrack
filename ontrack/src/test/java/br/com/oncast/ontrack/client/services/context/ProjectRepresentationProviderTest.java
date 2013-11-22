package br.com.oncast.ontrack.client.services.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.i18n.ClientMessages;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationListener;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.context.ProjectAddedEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectAddedEventHandler;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListResponse;
import br.com.oncast.ontrack.utils.mocks.callback.DispatchCallbackMock;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

public class ProjectRepresentationProviderTest {

	@Mock
	private DispatchService dispatch;

	@Mock
	private ServerPushClientService serverPush;

	@Mock
	private AuthenticationService auth;

	@Mock
	private ClientAlertingService alertingService;

	@Mock
	private ClientMessages clientErrorMessages;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(auth.isUserAvailable()).thenReturn(true);
	}

	@Test
	public void doesntUpdateAvaliableProjectsOnInitializationIfUserInformationIsUnavailable() throws Exception {
		when(auth.isUserAvailable()).thenReturn(false);
		createProvider();
		assureProjectsWereRequested(0);
	}

	@Test
	public void updateAvailableProjectsOnUserInformationLoad() throws Exception {
		when(auth.isUserAvailable()).thenReturn(false);
		createProvider();
		final ArgumentCaptor<UserAuthenticationListener> captor = ArgumentCaptor.forClass(UserAuthenticationListener.class);
		verify(auth).registerUserAuthenticationListener(captor.capture());
		captor.getValue().onUserInformationLoaded();
		assureProjectsWereRequested();
	}

	@Test
	public void updateAvaliableProjectsOnInitializationIfUserInformationIsAvailable() throws Exception {
		createProvider();
		assureProjectsWereRequested();
	}

	@Test
	public void nothingHappensIfSearchForAvaliableProjectsFails() throws Exception {
		failWhenProjectsListWereRequested();
		createProvider();
		assureProjectsWereRequested();
	}

	@Test
	public void updateAvaliableProjectsWhenAUserLogsIn() throws Exception {
		createProvider();
		logIn();
		assureProjectsWereRequested(2);
	}

	@Test
	public void cleanAvaliableProjectsWhenAUserLogsOut() throws Exception {
		respondOneProjectWhenProjectsListWereRequested();

		final TestProjectListChangeListener listener = registerAndGetProjectListChangeListener(createProvider());
		assertEquals(1, listener.projectRepresentations.size());

		logOut();
		assertEquals(0, listener.projectRepresentations.size());
	}

	@Test
	public void projectsListAvailabilityIsNotifiedWhenListenerIsRegistered() throws Exception {
		final ProjectListChangeListener projectListChangeListener = Mockito.mock(ProjectListChangeListener.class);
		createProvider().registerProjectListChangeListener(projectListChangeListener);

		verify(projectListChangeListener, times(1)).onProjectListAvailabilityChange(Mockito.anyBoolean());
	}

	@Test
	public void projectsListAvailabilityIsNotifiedNegativelyWhenListenerIsRegisteredAndListIsNotAvailable() throws Exception {
		final ProjectListChangeListener projectListChangeListener = Mockito.mock(ProjectListChangeListener.class);
		createProvider().registerProjectListChangeListener(projectListChangeListener);

		verify(projectListChangeListener, times(1)).onProjectListAvailabilityChange(false);
	}

	@Test
	public void projectsListAvailabilityIsNotifiedPositivelyWhenListenerIsRegisteredAndListIsAvailable() throws Exception {
		respondOneProjectWhenProjectsListWereRequested();

		final ProjectListChangeListener projectListChangeListener = Mockito.mock(ProjectListChangeListener.class);
		createProvider().registerProjectListChangeListener(projectListChangeListener);

		verify(projectListChangeListener, times(1)).onProjectListAvailabilityChange(true);
	}

	@Test
	public void projectsListAvailabilityIsNotifiedWhenAUserLogsOut() throws Exception {
		final ProjectListChangeListener projectListChangeListener = Mockito.mock(ProjectListChangeListener.class);
		createProvider().registerProjectListChangeListener(projectListChangeListener);

		logOut();
		verify(projectListChangeListener, times(2)).onProjectListAvailabilityChange(Mockito.anyBoolean());
	}

	@Test
	public void projectsListAvailabilityIsNotifiedNegativelyWhenAUserLogsOut() throws Exception {
		final ProjectListChangeListener projectListChangeListener = Mockito.mock(ProjectListChangeListener.class);
		createProvider().registerProjectListChangeListener(projectListChangeListener);

		logOut();

		final ArgumentCaptor<Boolean> availability = ArgumentCaptor.forClass(Boolean.class);
		verify(projectListChangeListener, times(2)).onProjectListAvailabilityChange(availability.capture());

		assertEquals(false, availability.getAllValues().get(0));
		assertEquals(false, availability.getAllValues().get(1));
	}

	@Test
	public void projectsListChangeIsNotifiedWhenProjectCreationEventIsNotified() throws Exception {
		final ArgumentCaptor<ProjectAddedEventHandler> captor = ArgumentCaptor.forClass(ProjectAddedEventHandler.class);
		when(serverPush.registerServerEventHandler(Mockito.same(ProjectAddedEvent.class), captor.capture())).thenReturn(null);

		final TestProjectListChangeListener listener = registerAndGetProjectListChangeListener(createProvider());
		assertEquals(0, listener.projectRepresentations.size());

		final ProjectAddedEventHandler event = captor.getValue();
		final ProjectRepresentation representation = ProjectTestUtils.createRepresentation();
		event.onEvent(new ProjectAddedEvent(representation));

		assertEquals(1, listener.projectRepresentations.size());
		assertTrue(listener.projectRepresentations.contains(representation));
	}

	private ProjectRepresentationProviderImpl createProvider() {
		return new ProjectRepresentationProviderImpl(dispatch, serverPush, auth, alertingService, clientErrorMessages);
	}

	@SuppressWarnings("unchecked")
	private void respondOneProjectWhenProjectsListWereRequested() {
		final List<ProjectRepresentation> projects = new ArrayList<ProjectRepresentation>();
		projects.add(ProjectTestUtils.createRepresentation());

		DispatchCallbackMock.callOnSuccessWith(new ProjectListResponse(projects)).when(dispatch)
				.dispatch(Mockito.any(ProjectListRequest.class), Mockito.any(DispatchCallback.class));
	}

	@SuppressWarnings("unchecked")
	private void failWhenProjectsListWereRequested() {
		DispatchCallbackMock.callOnFailureWith(new RuntimeException()).when(dispatch)
				.dispatch(Mockito.any(ProjectListRequest.class), Mockito.any(DispatchCallback.class));
	}

	private void logIn() {
		registerAndGetAuthenticationListener().onUserLoggedIn();
	}

	private void logOut() {
		registerAndGetAuthenticationListener().onUserLoggedOut();
	}

	private UserAuthenticationListener registerAndGetAuthenticationListener() {
		final ArgumentCaptor<UserAuthenticationListener> captor = ArgumentCaptor.forClass(UserAuthenticationListener.class);
		verify(auth).registerUserAuthenticationListener(captor.capture());
		return captor.getValue();
	}

	private void assureProjectsWereRequested() {
		assureProjectsWereRequested(1);
	}

	@SuppressWarnings("unchecked")
	private void assureProjectsWereRequested(final int expectedTimes) {
		verify(dispatch, times(expectedTimes)).dispatch(Mockito.any(ProjectListRequest.class), Mockito.any(DispatchCallback.class));
	}

	private TestProjectListChangeListener registerAndGetProjectListChangeListener(final ProjectRepresentationProviderImpl provider) {
		final TestProjectListChangeListener listener = new TestProjectListChangeListener();
		provider.registerProjectListChangeListener(listener);
		return listener;
	}

	private class TestProjectListChangeListener implements ProjectListChangeListener {

		private final Set<ProjectRepresentation> projectRepresentations = new HashSet<ProjectRepresentation>();

		@SuppressWarnings("unused")
		private boolean availability;

		@Override
		public void onProjectListChanged(final Set<ProjectRepresentation> representations) {
			this.projectRepresentations.clear();
			this.projectRepresentations.addAll(representations);
		}

		@Override
		public void onProjectListAvailabilityChange(final boolean isAvailable) {
			this.availability = isAvailable;
		}

		@Override
		public void onProjectNameUpdate(final ProjectRepresentation projectRepresentation) {}

	}

}
