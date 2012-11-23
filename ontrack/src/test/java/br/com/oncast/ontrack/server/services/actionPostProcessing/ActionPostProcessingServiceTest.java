package br.com.oncast.ontrack.server.services.actionPostProcessing;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseCreateAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

public class ActionPostProcessingServiceTest {

	ActionPostProcessingService postProcessingService;

	@Before
	public void setUp() throws Exception {
		postProcessingService = new ActionPostProcessingService();
	}

	@After
	public void tearDown() {}

	@Test
	public void postProcessingMultipleActionsShouldPostProcessEachAction() throws UnableToHandleActionException {
		postProcessingService = spy(new ActionPostProcessingService());

		final Project project = ProjectTestUtils.createProject();
		final ProjectContext projectContext = new ProjectContext(project);
		final ActionContext actionContext = mock(ActionContext.class);
		final List<ModelAction> someActions = ActionTestUtils.createSomeActions();

		postProcessingService.postProcessActions(projectContext, actionContext, someActions);
		verify(postProcessingService, times(someActions.size())).postProcessAction(eq(projectContext), eq(actionContext), any(ModelAction.class));
	}

	@Test
	public void postProcessingMultipleActionsShouldPostProcessEachActionOnce() throws UnableToHandleActionException {
		postProcessingService = spy(new ActionPostProcessingService());

		final Project project = ProjectTestUtils.createProject();
		final ProjectContext projectContext = new ProjectContext(project);
		final ActionContext actionContext = mock(ActionContext.class);
		final List<ModelAction> someActions = ActionTestUtils.createSomeActions();

		postProcessingService.postProcessActions(projectContext, actionContext, someActions);
		for (final ModelAction modelAction : someActions) {
			verify(postProcessingService, times(1)).postProcessAction(eq(projectContext), eq(actionContext), eq(modelAction));
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void actionThatDoesNotHavePostProcessorsRegisteredSHouldNotInvokeOtherProcessors() throws UnableToPostProcessActionException {
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext projectContext = new ProjectContext(project);
		final ActionContext actionContext = mock(ActionContext.class);

		final ActionPostProcessor<ModelAction> postProcessorMock = mock(ActionPostProcessor.class);
		final ReleaseCreateAction modelAction = new ReleaseCreateAction("");

		postProcessingService.registerPostProcessor(postProcessorMock, ScopeBindReleaseAction.class);
		postProcessingService.postProcessAction(projectContext, actionContext, modelAction);

		verify(postProcessorMock, times(0)).process(any(ModelAction.class), eq(actionContext), eq(projectContext));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void actionThatDoesHavePostProcessorsRegisteredSHouldInvokeProcessor() throws UnableToPostProcessActionException {
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext projectContext = new ProjectContext(project);
		final ActionContext actionContext = mock(ActionContext.class);

		final ActionPostProcessor<ModelAction> postProcessorMock = mock(ActionPostProcessor.class);
		final ReleaseRemoveAction modelAction = new ReleaseRemoveAction(new UUID());

		postProcessingService.registerPostProcessor(postProcessorMock, ReleaseRemoveAction.class);
		postProcessingService.postProcessAction(projectContext, actionContext, modelAction);

		verify(postProcessorMock, times(1)).process(eq(modelAction), eq(actionContext), eq(projectContext));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void actionThatHaveTwoPostProcessorsRegisteredSHouldInvokeBothProcessors() throws UnableToPostProcessActionException {
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext projectContext = new ProjectContext(project);
		final ActionContext actionContext = mock(ActionContext.class);

		final ActionPostProcessor<ModelAction> postProcessorMock1 = mock(ActionPostProcessor.class);
		final ActionPostProcessor<ModelAction> postProcessorMock2 = mock(ActionPostProcessor.class);
		final ReleaseRemoveAction modelAction = new ReleaseRemoveAction(new UUID());

		postProcessingService.registerPostProcessor(postProcessorMock1, ReleaseRemoveAction.class);
		postProcessingService.registerPostProcessor(postProcessorMock2, ReleaseRemoveAction.class);
		postProcessingService.postProcessAction(projectContext, actionContext, modelAction);

		verify(postProcessorMock1, times(1)).process(eq(modelAction), eq(actionContext), eq(projectContext));
		verify(postProcessorMock2, times(1)).process(eq(modelAction), eq(actionContext), eq(projectContext));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void postProcessorsRegisteredWithInterfaceShouldSHouldInvokeBothProcessors() throws UnableToPostProcessActionException {
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext projectContext = new ProjectContext(project);
		final ActionContext actionContext = mock(ActionContext.class);

		final ActionPostProcessor<ModelAction> postProcessorMock1 = mock(ActionPostProcessor.class);
		final ActionPostProcessor<ModelAction> postProcessorMock2 = mock(ActionPostProcessor.class);
		final ReleaseRemoveAction modelAction = new ReleaseRemoveAction(new UUID());

		postProcessingService.registerPostProcessor(postProcessorMock1, ReleaseRemoveAction.class);
		postProcessingService.registerPostProcessor(postProcessorMock2, ReleaseRemoveAction.class);
		postProcessingService.postProcessAction(projectContext, actionContext, modelAction);

		verify(postProcessorMock1, times(1)).process(eq(modelAction), eq(actionContext), eq(projectContext));
		verify(postProcessorMock2, times(1)).process(eq(modelAction), eq(actionContext), eq(projectContext));
	}
}
