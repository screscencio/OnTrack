package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.checklist.ChecklistService;
import br.com.oncast.ontrack.client.ui.generalwidgets.DefaultTextedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ChecklistAddItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRemoveItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRenameAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
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
	VerticalModelWidgetContainer<ChecklistItem, ChecklistItemWidget> items;

	@UiField
	HasClickHandlers addButton;

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
	public VerticalModelWidgetContainer<ChecklistItem, ChecklistItemWidget> createItemContainer() {
		return new VerticalModelWidgetContainer<ChecklistItem, ChecklistItemWidget>(
				new ModelWidgetFactory<ChecklistItem, ChecklistItemWidget>() {
					@Override
					public ChecklistItemWidget createWidget(final ChecklistItem modelBean) {
						return new ChecklistItemWidget(subjectId, checklist.getId(), modelBean);
					}
				},
				new ModelWidgetContainerListener() {
					@Override
					public void onUpdateComplete(final boolean hasChanged) {}
				});
	}

	@UiFactory
	public EditableLabel createChecklistTitle() {
		return new EditableLabel(new EditableLabelEditionHandler() {
			@Override
			public boolean onEditionRequest(final String text) {
				return text != null && !text.trim().isEmpty() && !checklist.getTitle().equals(text.trim());
			}
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

	@UiHandler("addButton")
	public void onAddItemClick(final ClickEvent e) {
		addItem();
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
			ClientServiceProvider.getInstance().getClientAlertingService().showWarning(messagtes.emptyChecklistItemError());
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
		return ClientServiceProvider.getInstance().getActionExecutionService();
	}

	private ChecklistService getChecklistService() {
		return ClientServiceProvider.getInstance().getChecklistService();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
				if (action instanceof ChecklistRenameAction && action.getReferenceId().equals(checklist.getId())) updateTitle();
				else if (action instanceof ChecklistAddItemAction || action instanceof ChecklistRemoveItemAction
						&& action.getReferenceId().equals(checklist.getId())) updateItems();
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
