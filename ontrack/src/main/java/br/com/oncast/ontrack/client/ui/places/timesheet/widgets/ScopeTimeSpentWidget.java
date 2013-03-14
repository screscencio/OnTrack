package br.com.oncast.ontrack.client.ui.places.timesheet.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareTimeSpentAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTimeSpentWidget implements IsWidget {

	private static final ScopeTimeSpentMessages MESSAGES = GWT.create(ScopeTimeSpentMessages.class);

	private float timeSpent = 0;

	EditableLabel editableLabel;

	public ScopeTimeSpentWidget(final Scope scope, final UserRepresentation user, final float timeSpent) {
		editableLabel = new EditableLabel(new EditableLabelEditionHandler() {
			@Override
			public boolean onEditionRequest(final String text) {
				try {
					final Float newAppointment = Float.valueOf(text.replace(',', '.'));
					if (newAppointment.equals(ScopeTimeSpentWidget.this.timeSpent)) return false;

					launchAction(scope.getId(), newAppointment);
					return true;
				}
				catch (final NumberFormatException e) {
					ClientServiceProvider.getInstance().getClientAlertingService().showWarning(MESSAGES.shoulBeAValidNumber());
					return false;
				}
			}

		});
		editableLabel.setWidth("40px");
		editableLabel.getElement().getStyle().setProperty("textAlign", "center");
		editableLabel.setReadOnly(!ClientServiceProvider.getCurrentUser().equals(user.getId()));

		setTimeSpent(timeSpent);
	}

	public void setTimeSpent(final float time) {
		this.timeSpent = time;
		editableLabel.setValue("" + time);
	}

	private void launchAction(final UUID scopeId, final Float newAppointment) {
		ClientServiceProvider.getInstance().getActionExecutionService().onUserActionExecutionRequest(new ScopeDeclareTimeSpentAction(scopeId, newAppointment));
	}

	@Override
	public Widget asWidget() {
		return editableLabel;
	}

}
