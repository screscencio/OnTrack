package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChecklistItemWidget extends Composite implements ModelWidget<ChecklistItem> {

	private static ChecklistItemWidgetUiBinder uiBinder = GWT.create(ChecklistItemWidgetUiBinder.class);

	interface ChecklistItemWidgetUiBinder extends UiBinder<Widget, ChecklistItemWidget> {}

	private ChecklistItem checklistItem;

	protected ChecklistItemWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ChecklistItemWidget(final ChecklistItem checklistItem) {
		this();
		this.checklistItem = checklistItem;
		update();
	}

	@UiField
	Label description;

	@Override
	public boolean update() {
		this.description.setText(checklistItem.getDescription());
		return false;
	}

	@Override
	public ChecklistItem getModelObject() {
		return checklistItem;
	}

}
