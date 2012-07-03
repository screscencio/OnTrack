package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChecklistWidget extends Composite implements ModelWidget<Checklist> {

	private static ChecklistWidgetUiBinder uiBinder = GWT.create(ChecklistWidgetUiBinder.class);

	interface ChecklistWidgetUiBinder extends UiBinder<Widget, ChecklistWidget> {}

	@UiField
	Label title;
	private final Checklist checklist;

	public ChecklistWidget(final Checklist checklist) {
		this.checklist = checklist;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@Override
	public boolean update() {
		title.setText(checklist.getTitle());
		return false;
	}

	@Override
	public Checklist getModelObject() {
		return checklist;
	}

}
