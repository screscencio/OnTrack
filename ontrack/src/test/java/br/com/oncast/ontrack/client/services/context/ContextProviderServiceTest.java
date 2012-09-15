package br.com.oncast.ontrack.client.services.context;

import static org.mockito.Mockito.verify;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationListener;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextResponse;
import br.com.oncast.ontrack.utils.mocks.callback.DispatchCallbackMock;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ContextProviderServiceTest {

	private static final UUID PROJECT_ID = new UUID();

	@Mock
	private ProjectRepresentationProviderImpl projectRepresentationProvider;

	@Mock
	private DispatchService requestDispatchService;

	@Mock
	private AuthenticationService authenticationService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void contextIsNotAvailableBeforeLoadsProjectContext() throws Exception {

		DispatchCallbackMock.callOnSuccessWith(new ProjectContextResponse(createDummyProject())).when(requestDispatchService)
				.dispatch(Mockito.any(ProjectContextRequest.class), Mockito.any(DispatchCallback.class));

		final ContextProviderServiceImpl contextProviderService = new ContextProviderServiceImpl(projectRepresentationProvider,
				requestDispatchService, authenticationService);

		Assert.assertFalse(contextProviderService.isContextAvailable(PROJECT_ID));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void makesContextAvailableAfterLoadsProjectContext() throws Exception {

		DispatchCallbackMock.callOnSuccessWith(new ProjectContextResponse(createDummyProject())).when(requestDispatchService)
				.dispatch(Mockito.any(ProjectContextRequest.class), Mockito.any(DispatchCallback.class));

		final ContextProviderServiceImpl contextProviderService = new ContextProviderServiceImpl(projectRepresentationProvider, requestDispatchService,
				authenticationService);

		contextProviderService.loadProjectContext(PROJECT_ID, Mockito.mock(ProjectContextLoadCallback.class));

		Assert.assertTrue(contextProviderService.isContextAvailable(PROJECT_ID));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void cleansContextWhenUserLogsOut() throws Exception {

		DispatchCallbackMock.callOnSuccessWith(new ProjectContextResponse(createDummyProject())).when(requestDispatchService)
				.dispatch(Mockito.any(ProjectContextRequest.class), Mockito.any(DispatchCallback.class));

		final ContextProviderServiceImpl contextProviderService = new ContextProviderServiceImpl(projectRepresentationProvider, requestDispatchService,
				authenticationService);

		contextProviderService.loadProjectContext(PROJECT_ID, Mockito.mock(ProjectContextLoadCallback.class));

		Assert.assertTrue(contextProviderService.isContextAvailable(PROJECT_ID));

		final ArgumentCaptor<UserAuthenticationListener> captor = ArgumentCaptor.forClass(UserAuthenticationListener.class);
		verify(authenticationService).registerUserAuthenticationListener(captor.capture());

		captor.getValue().onUserLoggedOut();

		Assert.assertFalse(contextProviderService.isContextAvailable(PROJECT_ID));
	}

	private Project createDummyProject() throws Exception {
		return ProjectTestUtils.createProject(new ProjectRepresentation(PROJECT_ID, ""), ScopeTestUtils.createScope(""), new Release("", new UUID()));
	}
}
