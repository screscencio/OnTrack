package br.com.oncast.ontrack.client.services.annotations;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.components.annotations.AnnotationsPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class AnnotationServiceImpl implements AnnotationService {

	private final ContextProviderService contextProviderService;
	private AnnotationsPanel annotationsPanel;

	public AnnotationServiceImpl(final ContextProviderService contextProviderService) {
		this.contextProviderService = contextProviderService;
	}

	@Override
	public void showAnnotationsFor(final Scope scope) {
		final AnnotationsPanel panel = getAnnotationPanel();
		panel.setScope(scope);

		PopupConfig.configPopup().popup(panel).pop();
	}

	protected AnnotationsPanel getAnnotationPanel() {
		return annotationsPanel == null ? annotationsPanel = new AnnotationsPanel() : annotationsPanel;
	}
}
