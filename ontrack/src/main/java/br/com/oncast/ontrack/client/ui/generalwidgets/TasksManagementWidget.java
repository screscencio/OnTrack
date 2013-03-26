package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer;
import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer.ContainerAlignment;
import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer.Orientation;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.details.TaskWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.details.TaskWidget.TaskWidgetClickListener;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TasksManagementWidget extends Composite implements Focusable, TaskWidgetClickListener {

	private static TasksManagementWidgetUiBinder uiBinder = GWT.create(TasksManagementWidgetUiBinder.class);

	interface TasksManagementWidgetUiBinder extends UiBinder<Widget, TasksManagementWidget> {}

	private static TasksManagementWidgetMessages messages = GWT.create(TasksManagementWidgetMessages.class);

	private Scope scope;

	private String lastCreatedTask = "";

	@UiField(provided = true)
	ModelWidgetContainer<Scope, TaskWidget> tasksList;

	@UiField
	SimplePanel taskListScroll;

	@UiField
	Button addTask;

	@UiField
	Button removeTask;

	@UiField
	PaddedTextBox newTaskDescription;

	private TaskWidget selectedTask;

	public TasksManagementWidget() {
		tasksList = createTasksList();
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	public void setSelected(final Scope scope) {
		setScope(scope);
	}

	@Override
	public void setFocus(final boolean focused) {
		newTaskDescription.setFocus(focused);
	}

	@UiHandler("focusPanel")
	void onFocusPanelFocus(final FocusEvent e) {
		setFocus(true);
	}

	@UiHandler("taskListScroll")
	void onTaskListScrollFocus(final FocusEvent e) {
		setFocus(true);
	}

	@UiHandler("newTaskDescription")
	void onTaskDescriptionKeyDown(final KeyDownEvent event) {
		if (selectedTask == null) return;

		int index = tasksList.getWidgetIndex(selectedTask);
		if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_DOWN) {
			index += 1;
		}
		else if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_UP) {
			index -= 1;
		}

		moveSelection(index);
		event.stopPropagation();
	}

	@UiHandler("newTaskDescription")
	void onTaskDescriptionKeyUp(final KeyUpEvent event) {
		event.stopPropagation();

		if (event.getNativeKeyCode() == KEY_ESCAPE) clearDescription();
		else if (event.getNativeKeyCode() == KEY_ENTER && hasNewTaskDescription()) createTask();
		else if (new Shortcut(BrowserKeyCodes.KEY_DELETE).with(ControlModifier.PRESSED).accepts(event.getNativeEvent())) removeSelectedTask();
		else updateSubmit();
	}

	@UiHandler("addTask")
	public void onAddTaskClick(final ClickEvent event) {
		createTask();
	}

	@UiHandler("removeTask")
	public void onRemoveTaskClick(final ClickEvent event) {
		removeSelectedTask();
	}

	private void removeSelectedTask() {
		if (selectedTask == null) return;
		launchAction(new ScopeRemoveAction(selectedTask.getModelObject().getId()));
	}

	private void createTask() {
		if (!isStory()) {
			showWarning(messages.noStorySelected());
			return;
		}

		lastCreatedTask = newTaskDescription.getText();
		if (!hasNewTaskDescription()) {
			showWarning(messages.emptyTaskDescription());
			return;
		}

		launchAction(scope.getRelease() != null ? new ScopeInsertChildAction(getScopeId(), lastCreatedTask) : new ScopeInsertSiblingDownAction(getScopeId(),
				lastCreatedTask));

		clearDescription();
	}

	private boolean hasNewTaskDescription() {
		final String text = newTaskDescription.getText();
		return text != null && !text.trim().isEmpty();
	}

	private void setScope(final Scope scope) {
		this.scope = scope;
		update();
		setFocus(true);
	}

	public void update() {
		newTaskDescription.setEnabled(isStory());

		tasksList.update(getTasks());
		clearSelection();
		if (tasksList.getWidgetCount() > 0 && lastCreatedTask.isEmpty()) selectTask(tasksList.getWidget(0));
		else selectLastTask();

		updateSubmit();
	}

	private void selectLastTask() {
		for (int i = 0; i < tasksList.getWidgetCount(); i++) {
			final TaskWidget widget = tasksList.getWidget(i);
			if (widget.getDescription().equals(lastCreatedTask)) {
				selectTask(widget);
				return;
			}
		}
	}

	private List<Scope> getTasks() {
		return isStory() ? scope.getAllLeafs() : new ArrayList<Scope>();
	}

	private void updateSubmit() {
		addTask.setEnabled(isStory() && hasNewTaskDescription());
	}

	private void clearDescription() {
		newTaskDescription.setText("");
		updateSubmit();
	}

	private boolean isStory() {
		return scope != null && scope.getRelease() != null;
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

	private ModelWidgetContainer<Scope, TaskWidget> createTasksList() {
		return new ModelWidgetContainer<Scope, TaskWidget>(new ModelWidgetFactory<Scope, TaskWidget>() {
			@Override
			public TaskWidget createWidget(final Scope modelBean) {
				return new TaskWidget(modelBean, TasksManagementWidget.this);
			}
		}, new AnimatedContainer(new VerticalPanel()));
	}

	@Override
	public int getTabIndex() {
		return newTaskDescription.getTabIndex();
	}

	@Override
	public void setAccessKey(final char key) {
		newTaskDescription.setAccessKey(key);
	}

	@Override
	public void setTabIndex(final int index) {
		newTaskDescription.setTabIndex(index);
	}

	@Override
	public void onClick(final TaskWidget task) {
		clearSelection();
		selectTask(task);
		setFocus(true);
	}

	private void moveSelection(final int index) {
		clearSelection();
		final int i = (tasksList.getWidgetCount() + index) % tasksList.getWidgetCount();
		selectTask(tasksList.getWidget(i));
	}

	private void clearSelection() {
		if (selectedTask == null) return;

		selectedTask.setTargetHighlight(false);
		selectedTask = null;
	}

	private void selectTask(final TaskWidget task) {
		selectedTask = task;
		selectedTask.setTargetHighlight(true);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				WidgetVisibilityEnsurer.ensureVisible(selectedTask.getElement(), taskListScroll.getElement(), Orientation.VERTICAL,
						ContainerAlignment.END, -50);
			}
		});
	}
}
