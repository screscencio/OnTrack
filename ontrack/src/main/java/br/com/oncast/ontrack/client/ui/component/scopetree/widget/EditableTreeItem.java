package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableTreeItem {

	private final String description;
	private Panel panel;
	private Label label;
	private TextBox editBox;

	public EditableTreeItem(final String description) {
		this.description = description;
	}

	public EditableTreeItem() {
		this.description = "";
	}

	public Widget asWidget() {
		return panel;
	}

	public void initComponent() {
		panel = new HorizontalPanel();
		label = new Label(this.description);
		panel.add(label);
		editBox = new TextBox();
		panel.add(editBox);
		label.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				editableMode(true);
			}

		});
	}

	private void editableMode(final boolean editable) {
		if (editable) {
			label.setVisible(false);
			editBox.setVisible(true);
			editBox.setFocus(true);

		} else {
			label.setVisible(true);
			editBox.setVisible(false);
		}
	}
}
