package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusEvent;
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
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DefaultTextedTextBox extends Composite implements HasText, HasKeyUpHandlers, HasKeyDownHandlers {

	private static DefaultTextedTextBoxUiBinder uiBinder = GWT.create(DefaultTextedTextBoxUiBinder.class);

	interface DefaultTextedTextBoxUiBinder extends UiBinder<Widget, DefaultTextedTextBox> {}

	public DefaultTextedTextBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label defaultText;

	@UiField
	TextBox textBox;

	@UiField
	FocusPanel focusPanel;

	@UiHandler("focusPanel")
	public void onFocus(final FocusEvent e) {
		textBox.setFocus(true);
	}

	@UiHandler("textBox")
	public void onKeyUp(final KeyUpEvent e) {
		updateDefaultText();
	}

	public void setDefaultText(final String text) {
		defaultText.setText(text);
	}

	public void setDefaultTextAlign(final String align) {
		defaultText.getElement().getStyle().setProperty("textAlign", align);
	}

	@Override
	public void setText(final String text) {
		textBox.setText(text);
		updateDefaultText();
	}

	private void updateDefaultText() {
		defaultText.setVisible(textBox.getText().isEmpty());
	}

	@Override
	public String getText() {
		return textBox.getText();
	}

	@Override
	public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
		return textBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(final KeyUpHandler handler) {
		return textBox.addKeyUpHandler(handler);
	}

	public void setCursorPos(final int pos) {
		textBox.setCursorPos(pos);
	}

	public void setFocus(final boolean focused) {
		textBox.setFocus(focused);
	}

	public void selectAll() {
		textBox.selectAll();
	}

	public void setEnabled(final boolean enabled) {
		textBox.setEnabled(enabled);
	}
}
