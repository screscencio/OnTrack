package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AddTaskWidget extends Composite implements Focusable {

	private static AddTaskWidgetUiBinder uiBinder = GWT.create(AddTaskWidgetUiBinder.class);

	interface AddTaskWidgetUiBinder extends UiBinder<Widget, AddTaskWidget> {}

	private static AddTaskWidgetMessages messages = GWT.create(AddTaskWidgetMessages.class);

	private Scope scope;

	@UiField
	Label label;

	@UiField
	Button submit;

	@UiField
	PaddedTextBox taskDescription;

	public AddTaskWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	public void setSelected(final Scope scope) {
		setScope(scope);
	}

	@Override
	public void setFocus(final boolean focused) {
		taskDescription.setFocus(focused);
	}

	@UiHandler("taskDescription")
	void onTaskDescriptionKeyUp(final KeyUpEvent event) {
		event.stopPropagation();

		if (event.getNativeKeyCode() == KEY_ESCAPE) clearDescription();
		else if (event.getNativeKeyCode() == KEY_ENTER && hasNewTaskDescription()) createTask();
		else updateSubmit();
	}

	@UiHandler("submit")
	public void onSubmitClick(final ClickEvent event) {
		createTask();
	}

	private void createTask() {
		if (!hasScope()) {
			showWarning(messages.noScopeSelected());
			return;
		}

		final String text = taskDescription.getText();
		if (!hasNewTaskDescription()) {
			showWarning(messages.emptyDescription());
			return;
		}

		launchAction(scope.getRelease() != null ? new ScopeInsertChildAction(getScopeId(), text) : new ScopeInsertSiblingDownAction(getScopeId(), text));
		clearDescription();
	}

	private boolean hasNewTaskDescription() {
		final String text = taskDescription.getText();
		return text != null && !text.trim().isEmpty();
	}

	private void setScope(final Scope scope) {
		this.scope = scope;
		update();
	}

	private void update() {
		label.setText(getCurrentTitle());
		taskDescription.setEnabled(hasScope());
		updateSubmit();
	}

	private void updateSubmit() {
		submit.setEnabled(hasScope() && hasNewTaskDescription());
	}

	private void clearDescription() {
		taskDescription.setText("");
		updateSubmit();
	}

	private String getCurrentTitle() {
		return hasScope() ? messages.addTaskTitle(scope.getDescription()) : messages.noScopeTitle();
	}

	private boolean hasScope() {
		return scope != null;
	}

	private UUID getScopeId() {
		return scope.getId();
	}

	private void showWarning(final String message) {
		ClientServiceProvider.getInstance().getClientAlertingService().showWarning(message);
	}

	private void launchAction(final ModelAction action) {
		ClientServiceProvider.getInstance().getActionExecutionService().onUserActionExecutionRequest(action);
	}

	@Override
	public int getTabIndex() {
		return taskDescription.getTabIndex();
	}

	@Override
	public void setAccessKey(final char key) {
		taskDescription.setAccessKey(key);
	}

	@Override
	public void setTabIndex(final int index) {
		taskDescription.setTabIndex(index);
	}

}
