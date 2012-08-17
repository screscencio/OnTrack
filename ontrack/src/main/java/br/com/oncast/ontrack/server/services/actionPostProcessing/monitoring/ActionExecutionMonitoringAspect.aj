package br.com.oncast.ontrack.server.services.actionPostProcessing.monitoring;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public aspect ActionExecutionMonitoringAspect {

	ThreadLocal<BooleanContainer> threadLocal;
	private ActionPostProcessingService postProcessingService;
	
	public ActionExecutionMonitoringAspect() {
		threadLocal = new ThreadLocal<BooleanContainer>();
	}
	
	private BooleanContainer getBooleanContainer() {
		if (threadLocal.get() == null) threadLocal.set(new BooleanContainer());
		return threadLocal.get();
	}
	
	public void setActionPostProcessingService(ActionPostProcessingService postProcessingService) {
		this.postProcessingService = postProcessingService;
	}
	
	pointcut executionMethod() : execution(public * ModelAction.execute(ProjectContext, ActionContext));
	pointcut annotatedDoProcessMethod() : execution(@PostProcessActions * *(..));
	pointcut annotatedDoNotProcessMethod() : execution(@DontPostProcessActions * *(..));

	before() : annotatedDoProcessMethod() {
		getBooleanContainer().putValue(true);
	}

	after() : annotatedDoProcessMethod() {
		getBooleanContainer().popValue();
	}

	before() : annotatedDoNotProcessMethod() {
		getBooleanContainer().putValue(false);
	}

	after() : annotatedDoNotProcessMethod() {
		getBooleanContainer().popValue();
	}

	after() returning : executionMethod() {
		if (!getBooleanContainer().getValue()) return;
		ModelAction action = (ModelAction)thisJoinPoint.getThis();
		
		Object[] args = thisJoinPoint.getArgs();
		ProjectContext projectContext = (ProjectContext) args[0];
		ActionContext actionContext = (ActionContext) args[1];
		
		if(postProcessingService != null) postProcessingService.postProcessAction(projectContext, actionContext, action);
	}
}
