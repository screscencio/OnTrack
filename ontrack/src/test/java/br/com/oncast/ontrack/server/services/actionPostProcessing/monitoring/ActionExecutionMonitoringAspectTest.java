package br.com.oncast.ontrack.server.services.actionPostProcessing.monitoring;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class ActionExecutionMonitoringAspectTest {

	private ActionContext actionContext;
	private ProjectContext projectContext;
	private ActionPostProcessor<FicticiousAction> postProcessor;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		postProcessor = Mockito.mock(ActionPostProcessor.class);
		final ActionPostProcessingService postProcessingService = new ActionPostProcessingService();
		postProcessingService.registerPostProcessor(postProcessor, FicticiousAction.class);

		final Project project = ProjectTestUtils.createProject();
		projectContext = new ProjectContext(project);
		actionContext = mock(ActionContext.class);
	}

	@Test
	public void shouldNotPostProcessActionExecutionInNonAnnotatedMethods() throws UnableToCompleteActionException {
		final FicticiousAction action = new FicticiousAction();
		executeAction(projectContext, actionContext, action);
		Mockito.verify(postProcessor, Mockito.times(0)).process(action, actionContext, projectContext);
	}

	@Test
	public void shouldNotPostProcessActionExecutionInDontPostProcessAnnotatedMethods() throws UnableToCompleteActionException {
		final FicticiousAction action = new FicticiousAction();
		executeActionWithoutPostProcessing(projectContext, actionContext, action);
		Mockito.verify(postProcessor, Mockito.times(0)).process(action, actionContext, projectContext);
	}

	@Test
	public void shouldPostProcessActionExecutionInPostProcessAnnotatedMethods() throws UnableToCompleteActionException {
		final FicticiousAction action = new FicticiousAction();
		executeActionWithPostProcessing(projectContext, actionContext, action);
		Mockito.verify(postProcessor, Mockito.times(1)).process(action, actionContext, projectContext);
	}

	@Test
	@PostProcessActions
	public void shouldNotPostProcessActionExecutionTakingInAccountNestedAnnotatedMethods() throws UnableToCompleteActionException {
		final FicticiousAction action = new FicticiousAction();
		executeActionWithoutPostProcessing(projectContext, actionContext, action);
		Mockito.verify(postProcessor, Mockito.times(0)).process(action, actionContext, projectContext);
	}

	@Test
	@DontPostProcessActions
	public void shouldPostProcessActionExecutionTakingInAccountNestedAnnotatedMethods() throws UnableToCompleteActionException {
		final FicticiousAction action = new FicticiousAction();
		executeActionWithPostProcessing(projectContext, actionContext, action);
		Mockito.verify(postProcessor, Mockito.times(1)).process(action, actionContext, projectContext);
	}

	@Test
	@DontPostProcessActions
	public void shouldPostProcessOnlyOneActionExecution() throws UnableToCompleteActionException {
		final FicticiousAction action = new FicticiousAction();
		executeActionWithPostProcessing(projectContext, actionContext, action);
		executeActionWithoutPostProcessing(projectContext, actionContext, action);
		executeAction(projectContext, actionContext, action);
		Mockito.verify(postProcessor, Mockito.times(1)).process(action, actionContext, projectContext);
	}

	@Test
	@PostProcessActions
	public void shouldPostProcessTwoActionExecution() throws UnableToCompleteActionException {
		final FicticiousAction action = new FicticiousAction();
		executeActionWithPostProcessing(projectContext, actionContext, action);
		executeActionWithoutPostProcessing(projectContext, actionContext, action);
		executeAction(projectContext, actionContext, action);
		Mockito.verify(postProcessor, Mockito.times(2)).process(action, actionContext, projectContext);
	}

	@DontPostProcessActions
	private void executeActionWithoutPostProcessing(final ProjectContext projectContext, final ActionContext actionContext, final FicticiousAction action) throws UnableToCompleteActionException {
		executeAction(projectContext, actionContext, action);
	}

	@PostProcessActions
	private void executeActionWithPostProcessing(final ProjectContext projectContext, final ActionContext actionContext, final FicticiousAction action) throws UnableToCompleteActionException {
		executeAction(projectContext, actionContext, action);
	}

	private void executeAction(final ProjectContext projectContext, final ActionContext actionContext, final FicticiousAction action) throws UnableToCompleteActionException {
		action.execute(projectContext, actionContext);
	}

	class FicticiousAction implements ModelAction {

		private static final long serialVersionUID = 1L;

		@Override
		public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
			return new FicticiousAction();
		}

		@Override
		public UUID getReferenceId() {
			return new UUID("");
		}

		@Override
		public UUID getId() {
			return new UUID("");
		}

	}

}
