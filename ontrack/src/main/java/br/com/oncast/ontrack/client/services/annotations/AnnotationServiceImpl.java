package br.com.oncast.ontrack.client.services.annotations;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.annotations.AnnotationsPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotationServiceImpl implements AnnotationService {

	private AnnotationsPanel annotationsPanel;

	@Override
	public void showAnnotationsFor(final Scope scope) {
		final AnnotationsPanel panel = getAnnotationPanel();
		panel.setScope(scope);

		PopupConfig.configPopup().popup(panel).pop();
	}

	protected AnnotationsPanel getAnnotationPanel() {
		return annotationsPanel == null ? annotationsPanel = new AnnotationsPanel() : annotationsPanel;
	}

	@Override
	public void createAnnotationFor(final UUID subjectId, final String message) {
		final ClientServiceProvider provider = ClientServiceProvider.getInstance();
		final User user = provider.getAuthenticationService().getCurrentUser();
		provider.getActionExecutionService().onUserActionExecutionRequest(new AnnotationCreateAction(subjectId, user, message));
	}
}
