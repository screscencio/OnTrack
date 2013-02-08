package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RemoveTaskWidget extends Composite {

	private static RemoveTaskWidgetUiBinder uiBinder = GWT.create(RemoveTaskWidgetUiBinder.class);

	interface RemoveTaskWidgetUiBinder extends UiBinder<Widget, RemoveTaskWidget> {}

	private static RemoveTaskWidgetMessages messages = GWT.create(RemoveTaskWidgetMessages.class);

	private Scope scope;

	@UiField
	Label label;

	@UiField
	Button submit;

	public RemoveTaskWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	public void setSelected(final Scope scope) {
		setScope(scope);
	}

	@UiHandler("submit")
	public void onSubmitClick(final ClickEvent event) {
		removeTask();
	}

	private void setScope(final Scope scope) {
		this.scope = scope;
		update();
	}

	private void removeTask() {
		if (isTask()) ClientServiceProvider.getInstance().getActionExecutionService().onUserActionExecutionRequest(new ScopeRemoveAction(scope.getId()));
		else ClientServiceProvider.getInstance().getClientAlertingService().showWarning(messages.noTaskSelected());
	}

	private void updateSubmit() {
		submit.setEnabled(isTask());
	}

	private void update() {
		label.setText(getCurrentTitle());
		updateSubmit();
	}

	private String getCurrentTitle() {
		return isTask() ? messages.removeTaskTitle(scope.getDescription()) : messages.noTaskTitle();
	}

	private boolean isTask() {
		return scope != null && scope.getRelease() == null;
	}

}
