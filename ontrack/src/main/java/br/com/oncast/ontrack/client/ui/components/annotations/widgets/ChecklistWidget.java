package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.checklist.ChecklistService;
import br.com.oncast.ontrack.client.ui.generalwidgets.DefaultTextedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ChecklistAddItemAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
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

	private static ChecklistWidgetUiBinder uiBinder = GWT.create(ChecklistWidgetUiBinder.class);

	interface ChecklistWidgetUiBinder extends UiBinder<Widget, ChecklistWidget> {}

	@UiField
	FocusPanel rootPanel;

	@UiField
	Label title;

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
						return new ChecklistItemWidget(modelBean);
					}
				},
				new ModelWidgetContainerListener() {
					@Override
					public void onUpdateComplete(final boolean hasChanged) {}
				});
	}

	public ChecklistWidget(final UUID subjectId, final Checklist checklist) {
		this.subjectId = subjectId;
		this.checklist = checklist;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@UiHandler("rootPanel")
	public void onMouseOver(final MouseOverEvent e) {
		addItemDeck.setVisible(true);
	}

	@UiHandler("rootPanel")
	public void onMouseOut(final MouseOutEvent e) {
		hideAddItemDeck();
	}

	@UiHandler("addItemLabel")
	public void onAddItemLabelClick(final ClickEvent e) {
		addItemDeck.showWidget(1);
		newItemDescription.setFocus(true);
	}

	@UiHandler("addButton")
	public void onAddItemClick(final ClickEvent e) {
		addItem();
	}

	@UiHandler("newItemDescription")
	public void onKeyDown(final KeyDownEvent e) {
		if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ENTER) addItem();
		if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) {
			hideNewItemDescription();
			e.stopPropagation();
		}

	}

	private void addItem() {
		final String itemDescription = newItemDescription.getText().trim();
		if (itemDescription.isEmpty()) {
			ClientServiceProvider.getInstance().getClientNotificationService().showWarning("Can't create a item with empty description.");
			newItemDescription.setFocus(true);
			return;
		}

		getChecklistService().addCheckistItem(checklist.getId(), subjectId, itemDescription);
		hideNewItemDescription();
		newItemDescription.setText("");
	}

	private void hideNewItemDescription() {
		addItemDeck.showWidget(0);
		hideAddItemDeck();
	}

	@Override
	protected void onLoad() {
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
		hideNewItemDescription();
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
	}

	private void hideAddItemDeck() {
		final boolean isEmpty = items.getWidgetCount() == 0;
		final boolean isOnCreation = addItemDeck.getVisibleWidget() == 1;
		addItemDeck.setVisible(isEmpty || isOnCreation);
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
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof ChecklistAddItemAction && action.getReferenceId().equals(checklist.getId())) updateItems();
			}
		};
		return actionExecutionListener;
	}

	@Override
	public boolean update() {
		title.setText(checklist.getTitle());
		updateItems();
		return false;
	}

	private void updateItems() {
		items.update(checklist.getItems());
	}

	@Override
	public Checklist getModelObject() {
		return checklist;
	}

}
