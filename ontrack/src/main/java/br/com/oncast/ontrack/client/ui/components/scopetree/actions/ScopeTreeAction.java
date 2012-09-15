package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public interface ScopeTreeAction {
	// TODO Review if 'isUserInteraction' actions should be processed as other method and if it should be called externally or internally.
	void execute(final ProjectContext context, ActionContext actionContext, boolean isUserInteraction) throws ModelBeanNotFoundException;
}
