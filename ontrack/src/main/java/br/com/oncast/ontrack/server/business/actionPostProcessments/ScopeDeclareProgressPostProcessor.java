package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ScopeDeclareProgressPostProcessor implements ActionPostProcessor<ScopeDeclareProgressAction> {

	@Override
	public void process(final ScopeDeclareProgressAction action, final ActionContext actionContext, final ProjectContext projectContext) {
		action.setTimestamp(actionContext.getTimestamp());
	}

}
