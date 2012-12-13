package br.com.oncast.ontrack.client.services.context;

import static org.junit.Assert.assertFalse;
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
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

public class ContextProviderServiceTest {

	private UUID projectId;

	@Mock
	private ProjectRepresentationProviderImpl projectRepresentationProvider;

	@Mock
	private DispatchService requestDispatchService;

	@Mock
	private AuthenticationService authenticationService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		projectId = new UUID();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void contextIsNotAvailableBeforeLoadsProjectContext() throws Exception {

		DispatchCallbackMock.callOnSuccessWith(new ProjectContextResponse(createDummyProject())).when(requestDispatchService)
				.dispatch(Mockito.any(ProjectContextRequest.class), Mockito.any(DispatchCallback.class));

		final ContextProviderServiceImpl contextProviderService = new ContextProviderServiceImpl(projectRepresentationProvider,
				requestDispatchService, authenticationService);

		Assert.assertFalse(contextProviderService.isContextAvailable(projectId));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void makesContextAvailableAfterLoadsProjectContext() throws Exception {

		DispatchCallbackMock.callOnSuccessWith(new ProjectContextResponse(createDummyProject())).when(requestDispatchService)
				.dispatch(Mockito.any(ProjectContextRequest.class), Mockito.any(DispatchCallback.class));

		final ContextProviderServiceImpl contextProviderService = new ContextProviderServiceImpl(projectRepresentationProvider, requestDispatchService,
				authenticationService);

		contextProviderService.loadProjectContext(projectId, Mockito.mock(ProjectContextLoadCallback.class));

		Assert.assertTrue(contextProviderService.isContextAvailable(projectId));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void cleansContextWhenUserLogsOut() throws Exception {

		DispatchCallbackMock.callOnSuccessWith(new ProjectContextResponse(createDummyProject())).when(requestDispatchService)
				.dispatch(Mockito.any(ProjectContextRequest.class), Mockito.any(DispatchCallback.class));

		final ContextProviderServiceImpl contextProviderService = new ContextProviderServiceImpl(projectRepresentationProvider, requestDispatchService,
				authenticationService);

		contextProviderService.loadProjectContext(projectId, Mockito.mock(ProjectContextLoadCallback.class));

		Assert.assertTrue(contextProviderService.isContextAvailable(projectId));

		final ArgumentCaptor<UserAuthenticationListener> captor = ArgumentCaptor.forClass(UserAuthenticationListener.class);
		verify(authenticationService).registerUserAuthenticationListener(captor.capture());

		captor.getValue().onUserLoggedOut();

		Assert.assertFalse(contextProviderService.isContextAvailable(projectId));
	}

	@Test
	public void shouldBeAbleToUnloadTheCurrentProjectContext() throws Exception {
		final ContextProviderServiceImpl service = new ContextProviderServiceImpl(projectRepresentationProvider, requestDispatchService, authenticationService);
		service.loadProjectContext(projectId, Mockito.mock(ProjectContextLoadCallback.class));

		service.unloadProjectContext();
		assertFalse(service.isContextAvailable(projectId));
	}

	private Project createDummyProject() throws Exception {
		return ProjectTestUtils.createProject(new ProjectRepresentation(projectId, ""), ScopeTestUtils.createScope(""), new Release("", new UUID()));
	}
}
