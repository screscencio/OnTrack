package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.DefaultTextedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ChecklistAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChecklistsContainerWidget extends Composite {

	private static final DetailPanelMessages messages = GWT.create(DetailPanelMessages.class);

	private static ChecklistsContainerWidgetUiBinder uiBinder = GWT.create(ChecklistsContainerWidgetUiBinder.class);

	interface ChecklistsContainerWidgetUiBinder extends UiBinder<Widget, ChecklistsContainerWidget> {}

	@UiField
	HorizontalPanel addContainer;

	@UiField
	HasClickHandlers addButton;

	@UiField
	ModelWidgetContainer<Checklist, ChecklistWidget> checklists;

	@UiField
	DefaultTextedTextBox newChecklistTitle;

	private UUID subjectId;

	private ActionExecutionListener actionExecutionListener;

	final Set<EditionListener> editionListeners = new HashSet<EditionListener>();

	private boolean justCreatedAnChecklist = false;

	@UiFactory
	protected ModelWidgetContainer<Checklist, ChecklistWidget> createChecklists() {
		return new ModelWidgetContainer<Checklist, ChecklistWidget>(new ModelWidgetFactory<Checklist, ChecklistWidget>() {
			@Override
			public ChecklistWidget createWidget(final Checklist modelBean) {
				final ChecklistWidget checklistWidget = new ChecklistWidget(subjectId, modelBean);
				if (justCreatedAnChecklist) {
					checklistWidget.enterCreateItemMode();
					justCreatedAnChecklist = false;
				}
				return checklistWidget;
			}
		});
	}

	public ChecklistsContainerWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad() {
		hideNewChecklistTitle(false);
		getProvider().actionExecution().addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	protected void onUnload() {
		getProvider().actionExecution().removeActionExecutionListener(getActionExecutionListener());
	}

	@UiHandler("newChecklistTitle")
	void onKeyDown(final KeyDownEvent e) {
		e.stopPropagation();

		if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ENTER) createChecklist();
		else if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) hideNewChecklistTitle(false);
	}

	@UiHandler("addButton")
	void onAddButtonClick(final ClickEvent e) {
		createChecklist();
	}

	public void enterEditMode() {
		addContainer.setVisible(true);
		newChecklistTitle.setFocus(true);
	}

	public void enterEditMode(final EditionListener listener) {
		editionListeners.add(listener);
		addContainer.setVisible(true);
		newChecklistTitle.setFocus(true);
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isUserAction) {
				final ModelAction action = execution.getModelAction();
				if (action instanceof ChecklistAction && action.getReferenceId().equals(subjectId)) {
					justCreatedAnChecklist = isUserAction && (action instanceof ChecklistCreateAction);
					update();
				}
			}
		};
		return actionExecutionListener;
	}

	private void createChecklist() {
		final String checklistTitle = this.newChecklistTitle.getText();
		if (checklistTitle.trim().isEmpty()) {
			ClientServices.get().alerting().showWarning(messages.emptyChecklistTitleError());
			newChecklistTitle.setFocus(true);
			return;
		}

		getProvider().checklists().addChecklist(subjectId, checklistTitle);
		hideNewChecklistTitle(true);
		newChecklistTitle.setText("");
	}

	private void hideNewChecklistTitle(final boolean editionSubmitted) {
		for (final EditionListener listener : new HashSet<EditionListener>(editionListeners)) {
			listener.onEditionEnd(editionSubmitted);
			editionListeners.remove(listener);
		}
		addContainer.setVisible(false);
	}

	public void setSubjectId(final UUID subjectId) {
		this.subjectId = subjectId;
		update();
	}

	private void update() {
		this.checklists.update(ClientServices.getCurrentProjectContext().findChecklistsFor(subjectId));
	}

	private ClientServices getProvider() {
		return ClientServices.get();
	}

	public int getChecklistCount() {
		return checklists.getWidgetCount();
	}

	public interface EditionListener {
		public void onEditionEnd(boolean editionSubmitted);
	}

}
