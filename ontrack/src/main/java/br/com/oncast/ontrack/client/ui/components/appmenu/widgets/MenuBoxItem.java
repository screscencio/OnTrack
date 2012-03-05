package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MenuBoxItem extends Composite implements HasText, HasValue<String> {

	private static MenuBoxItemUiBinder uiBinder = GWT.create(MenuBoxItemUiBinder.class);

	interface MenuBoxItemUiBinder extends UiBinder<Widget, MenuBoxItem> {}

	interface MenuBoxItemStyle extends CssResource {
		String selected();
	}

	public MenuBoxItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	FocusPanel rootPanel;

	@UiField
	Label label;

	private Command command;

	private String value;

	@UiField
	MenuBoxItemStyle style;

	public MenuBoxItem(final String text, final Command command) {
		this(text, text, command);
	}

	public MenuBoxItem(final String text, final String value, final Command command) {
		this.value = value;
		this.command = command;

		initWidget(uiBinder.createAndBindUi(this));
		this.label.setText(text);
	}

	public void executeCommand() {
		command.execute();
	}

	@UiHandler("rootPanel")
	void onClick(final ClickEvent e) {
		executeCommand();
	}

	@Override
	public void setText(final String text) {
		label.setText(text);
	}

	@Override
	public String getText() {
		return label.getText();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
		throw new RuntimeException("This Component doesen't suport handlers, Maybe you are using it in wrong place");
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(final String value) {
		setValue(value, false);
	}

	@Override
	public void setValue(final String value, final boolean fireEvents) {
		this.value = value;
	}

	public void setSelected(final boolean b) {
		rootPanel.setStyleName(style.selected(), b);
	}

}
