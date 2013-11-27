package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.checklist.ChecklistService;
import br.com.oncast.ontrack.client.ui.generalwidgets.DefaultTextedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ChecklistAddItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRemoveItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRenameAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChecklistWidget extends Composite implements ModelWidget<Checklist> {

	private static final DetailPanelMessages messagtes = GWT.create(DetailPanelMessages.class);

	private static ChecklistWidgetUiBinder uiBinder = GWT.create(ChecklistWidgetUiBinder.class);

	interface ChecklistWidgetUiBinder extends UiBinder<Widget, ChecklistWidget> {}

	@UiField
	FocusPanel rootPanel;

	@UiField
	EditableLabel title;

	@UiField
	Label remove;

	@UiField
	ModelWidgetContainer<ChecklistItem, ChecklistItemWidget> items;

	@UiField
	Label addItemLabel;

	@UiField
	DeckPanel addItemDeck;

	@UiField
	DefaultTextedTextBox newItemDescription;

	private final Checklist checklist;

	private ActionExecutionListener actionExecutionListener;

	private final UUID subjectId;

	@UiFactory
	public ModelWidgetContainer<ChecklistItem, ChecklistItemWidget> createItemContainer() {
		return new ModelWidgetContainer<ChecklistItem, ChecklistItemWidget>(new ModelWidgetFactory<ChecklistItem, ChecklistItemWidget>() {
			@Override
			public ChecklistItemWidget createWidget(final ChecklistItem modelBean) {
				return new ChecklistItemWidget(subjectId, checklist.getId(), modelBean);
			}
		});
	}

	@UiFactory
	public EditableLabel createChecklistTitle() {
		return new EditableLabel(new EditableLabelEditionHandler() {
			@Override
			public boolean onEditionRequest(final String text) {
				return text != null && !text.trim().isEmpty() && !checklist.getTitle().equals(text.trim());
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}

			@Override
			public void onEditionStart() {}

		}, true);
	}

	public ChecklistWidget(final UUID subjectId, final Checklist checklist) {
		this.subjectId = subjectId;
		this.checklist = checklist;
		initWidget(uiBinder.createAndBindUi(this));
		update();
		hideNewItemDescription();
		hideRemove();
	}

	@UiHandler("title")
	public void onTitleChange(final ValueChangeEvent<String> event) {
		getChecklistService().renameChecklist(subjectId, checklist.getId(), event.getValue());
	}

	@UiHandler("rootPanel")
	public void onMouseOver(final MouseOverEvent e) {
		remove.getElement().getStyle().setVisibility(Visibility.VISIBLE);
	}

	@UiHandler("rootPanel")
	public void onMouseOut(final MouseOutEvent e) {
		hideRemove();
	}

	private void hideRemove() {
		remove.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}

	@UiHandler("remove")
	public void onRemoveClicked(final ClickEvent e) {
		getChecklistService().removeChecklist(subjectId, checklist.getId());
	}

	@UiHandler("addItemLabel")
	public void onAddItemLabelClick(final ClickEvent e) {
		enterCreateItemMode();
	}

	@UiHandler("newItemIcon")
	public void onAddItemClick(final ClickEvent e) {
		if (addItemDeck.getVisibleWidget() == 0) enterCreateItemMode();
		else addItem();
	}

	@UiHandler("newItemDescription")
	public void onKeyDown(final KeyDownEvent e) {
		e.stopPropagation();

		if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ENTER) addItem();
		else if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) hideNewItemDescription();

	}

	private void addItem() {
		final String itemDescription = newItemDescription.getText().trim();
		if (itemDescription.isEmpty()) {
			ClientServices.get().alerting().showWarning(messagtes.emptyChecklistItemError());
			newItemDescription.setFocus(true);
			return;
		}

		getChecklistService().addCheckistItem(checklist.getId(), subjectId, itemDescription);
		newItemDescription.setText("");
		newItemDescription.setFocus(true);
	}

	private void hideNewItemDescription() {
		addItemDeck.showWidget(0);
		rootPanel.setFocus(true);
	}

	@Override
	protected void onLoad() {
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
		newItemDescription.setFocus(true);
	}

	@Override
	protected void onUnload() {
		hideRemove();
		hideNewItemDescription();
		getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
	}

	private ActionExecutionService getActionExecutionService() {
		return ClientServices.get().actionExecution();
	}

	private ChecklistService getChecklistService() {
		return ClientServices.get().checklists();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isUserAction) {
				final ModelAction action = execution.getModelAction();
				if (action instanceof ChecklistRenameAction && action.getReferenceId().equals(checklist.getId())) updateTitle();
				else if (action instanceof ChecklistAddItemAction || action instanceof ChecklistRemoveItemAction && action.getReferenceId().equals(checklist.getId())) updateItems();
			}
		};
		return actionExecutionListener;
	}

	@Override
	public boolean update() {
		updateTitle();
		updateItems();
		return false;
	}

	private void updateTitle() {
		title.setValue(checklist.getTitle(), false);
	}

	private void updateItems() {
		items.update(checklist.getItems());
	}

	@Override
	public Checklist getModelObject() {
		return checklist;
	}

	public void enterCreateItemMode() {
		addItemDeck.showWidget(1);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				newItemDescription.setFocus(true);
			}
		});
	}
}
