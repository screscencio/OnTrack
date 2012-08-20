package br.com.oncast.ontrack.server.business.actionPostProcessments;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ScopeDeclareProgressPostProcessor implements ActionPostProcessor<ScopeDeclareProgressAction> {

	private static final Logger LOGGER = Logger.getLogger(ScopeDeclareProgressPostProcessor.class);

	@Override
	public void process(final ScopeDeclareProgressAction action, final ActionContext actionContext, final ProjectContext projectContext) {
		LOGGER.debug("Executing Post processor '" + this.getClass().getSimpleName() + "' for '" + action.getClass().getSimpleName() + "' (" + action.toString()
				+ ").");
		action.setTimestamp(actionContext.getTimestamp());
	}

}
