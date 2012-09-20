package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.DefaultTextedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ChecklistAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChecklistsContainerWidget extends Composite {

	private static final DetailPanelMessages messages = GWT.create(DetailPanelMessages.class);

	private static ChecklistsContainerWidgetUiBinder uiBinder = GWT.create(ChecklistsContainerWidgetUiBinder.class);

	interface ChecklistsContainerWidgetUiBinder extends UiBinder<Widget, ChecklistsContainerWidget> {}

	@UiField
	Label addChecklistLabel;

	@UiField
	HasClickHandlers addButton;

	@UiField
	DeckPanel addChecklistDeck;

	@UiField
	VerticalModelWidgetContainer<Checklist, ChecklistWidget> checklists;

	@UiField
	DefaultTextedTextBox newChecklistTitle;

	private UUID subjectId;

	private ActionExecutionListener actionExecutionListener;

	private boolean justCreatedAnChecklist = false;

	@UiFactory
	protected VerticalModelWidgetContainer<Checklist, ChecklistWidget> createChecklists() {
		return new VerticalModelWidgetContainer<Checklist, ChecklistWidget>(new ModelWidgetFactory<Checklist, ChecklistWidget>() {
			@Override
			public ChecklistWidget createWidget(final Checklist modelBean) {
				final ChecklistWidget checklistWidget = new ChecklistWidget(subjectId, modelBean);
				if (justCreatedAnChecklist) {
					checklistWidget.enterCreateItemMode();
					justCreatedAnChecklist = false;
				}
				return checklistWidget;
			}
		}, new ModelWidgetContainerListener() {

			@Override
			public void onUpdateComplete(final boolean hasChanged) {}
		});
	}

	public ChecklistsContainerWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad() {
		hideNewChecklistTitle();
		getProvider().getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	protected void onUnload() {
		getProvider().getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
	}

	@UiHandler("newChecklistTitle")
	void onKeyDown(final KeyDownEvent e) {
		e.stopPropagation();

		if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ENTER) createChecklist();

		else if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) hideNewChecklistTitle();

	}

	@UiHandler("addButton")
	void onAddButtonClick(final ClickEvent e) {
		createChecklist();
	}

	@UiHandler("addChecklistLabel")
	void onAddChecklistLabelClick(final ClickEvent e) {
		addChecklistDeck.showWidget(1);
		newChecklistTitle.setFocus(true);
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
				if (action instanceof ChecklistAction && action.getReferenceId().equals(subjectId)) {
					justCreatedAnChecklist = isUserAction && (action instanceof ChecklistCreateAction);
					checklists.update(context.findChecklistsFor(subjectId));
				}
			}
		};
		return actionExecutionListener;
	}

	private void createChecklist() {
		final String checklistTitle = this.newChecklistTitle.getText();
		if (checklistTitle.trim().isEmpty()) {
			ClientServiceProvider.getInstance().getClientNotificationService().showWarning(messages.emptyChecklistTitleError());
			newChecklistTitle.setFocus(true);
			return;
		}

		getProvider().getChecklistService().addChecklist(subjectId, checklistTitle);
		hideNewChecklistTitle();
		newChecklistTitle.setText("");
	}

	private void hideNewChecklistTitle() {
		addChecklistDeck.showWidget(0);
	}

	public void setSubjectId(final UUID subjectId) {
		this.subjectId = subjectId;
		checklists.update(getProvider().getContextProviderService().getCurrentProjectContext().findChecklistsFor(subjectId));
	}

	private ClientServiceProvider getProvider() {
		return ClientServiceProvider.getInstance();
	}

}
