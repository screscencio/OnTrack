package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

public class PaddedPasswordTextBox extends Composite implements HasKeyDownHandlers, HasKeyUpHandlers, HasWidgets {

	private static PaddedPasswordTextBoxUiBinder uiBinder = GWT.create(PaddedPasswordTextBoxUiBinder.class);

	interface PaddedPasswordTextBoxUiBinder extends UiBinder<Widget, PaddedPasswordTextBox> {}

	public PaddedPasswordTextBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HTMLPanel container;

	@UiField
	PasswordTextBox passwordTextBox;

	@UiField
	FocusPanel focusPanel;

	@UiHandler("focusPanel")
	public void onFocus(final FocusEvent e) {
		passwordTextBox.setFocus(true);
	}

	public void setText(final String text) {
		passwordTextBox.setText(text);
	}

	public String getText() {
		return passwordTextBox.getText();
	}

	@Override
	public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
		return passwordTextBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(final KeyUpHandler handler) {
		return passwordTextBox.addKeyUpHandler(handler);
	}

	public void setFocus(final boolean focused) {
		passwordTextBox.setFocus(focused);
	}

	public void setEnabled(final boolean enabled) {
		passwordTextBox.setEnabled(enabled);
	}

	@Override
	public void add(final Widget w) {
		container.add(w);
	}

	@Override
	public void clear() {
		container.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return container.iterator();
	}

	@Override
	public boolean remove(final Widget w) {
		return container.remove(w);
	}

}
