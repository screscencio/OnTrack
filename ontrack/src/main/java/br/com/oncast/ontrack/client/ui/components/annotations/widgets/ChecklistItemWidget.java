package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.checklist.ChecklistService;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.action.ChecklistAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChecklistItemWidget extends Composite implements ModelWidget<ChecklistItem> {

	private static ChecklistItemWidgetUiBinder uiBinder = GWT.create(ChecklistItemWidgetUiBinder.class);

	interface ChecklistItemWidgetUiBinder extends UiBinder<Widget, ChecklistItemWidget> {}

	@UiField
	FocusPanel focusPanel;

	@UiField
	Label description;

	@UiField
	Label remove;

	@UiField
	CheckBox checkbox;

	private ChecklistItem checklistItem;

	private ActionExecutionListener actionExecutionListener;

	private UUID subjectId;

	private UUID checklistId;

	protected ChecklistItemWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ChecklistItemWidget(final UUID subjectId, final UUID checklistId, final ChecklistItem checklistItem) {
		this();
		this.subjectId = subjectId;
		this.checklistId = checklistId;
		this.checklistItem = checklistItem;
		update();
	}

	@UiHandler("focusPanel")
	public void onFocusPanelMouseOver(final MouseOverEvent e) {
		remove.setVisible(true);
	}

	@UiHandler("focusPanel")
	public void onFocusPanelMouseOut(final MouseOutEvent e) {
		remove.setVisible(false);
	}

	@UiHandler("remove")
	public void onRemoveClicked(final ClickEvent e) {
		getChecklistService().removeItem(subjectId, checklistId, checklistItem.getId());
	}

	@UiHandler("checkbox")
	public void onValueChanged(final ValueChangeEvent<Boolean> event) {
		getChecklistService().setItemChecked(subjectId, checklistId, checklistItem.getId(), event.getValue());
	}

	@Override
	protected void onLoad() {
		remove.setVisible(false);
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
	}

	@Override
	public boolean update() {
		this.description.setText(checklistItem.getDescription());

		if (this.checkbox.getValue() == checklistItem.isChecked()) return false;

		this.checkbox.setValue(checklistItem.isChecked());
		return true;
	}

	@Override
	public ChecklistItem getModelObject() {
		return checklistItem;
	}

	private ChecklistService getChecklistService() {
		return ClientServiceProvider.getInstance().getChecklistService();
	}

	private ActionExecutionService getActionExecutionService() {
		return ClientServiceProvider.getInstance().getActionExecutionService();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof ChecklistAction && action.getReferenceId().equals(checklistItem.getId())) update();
			}
		};
		return actionExecutionListener;
	}

}