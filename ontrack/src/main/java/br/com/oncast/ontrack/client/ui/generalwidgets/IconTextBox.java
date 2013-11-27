package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

public class IconTextBox extends Composite implements HasText, Focusable, HasKeyDownHandlers, HasKeyUpHandlers {

	private static IconTextBoxUiBinder uiBinder = GWT.create(IconTextBoxUiBinder.class);

	interface IconTextBoxUiBinder extends UiBinder<Widget, IconTextBox> {}

	@UiField
	DivElement helpLabel;

	@UiField
	SpanElement icon;

	@UiField
	TextBox textBox;

	public IconTextBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("textBox")
	protected void handleKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() == KEY_ESCAPE) {
			textBox.setText("");
		}
		setHelpLabelVisible(textBox.getText().isEmpty());
	}

	public void setHelpLabelVisible(final boolean visible) {
		if (visible) helpLabel.getStyle().clearDisplay();
		else helpLabel.getStyle().setDisplay(Display.NONE);
	}

	public void setIconStyle(final String iconStyle) {
		icon.addClassName(iconStyle);
	}

	public void setHelpText(final String text) {
		helpLabel.setInnerText(text);
	}

	@Override
	public void setText(final String text) {
		textBox.setText(text);
	}

	@Override
	public String getText() {
		return textBox.getText();
	}

	@Override
	public HandlerRegistration addKeyUpHandler(final KeyUpHandler handler) {
		return textBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
		return textBox.addKeyDownHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return textBox.getTabIndex();
	}

	@Override
	public void setAccessKey(final char key) {
		textBox.setAccessKey(key);
	}

	@Override
	public void setFocus(final boolean focused) {
		textBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(final int index) {
		textBox.setTabIndex(index);
	}

}
