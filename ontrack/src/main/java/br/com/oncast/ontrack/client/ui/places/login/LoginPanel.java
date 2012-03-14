package br.com.oncast.ontrack.client.ui.places.login;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import br.com.oncast.ontrack.client.ui.generalwidgets.PaddedPasswordTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.PaddedTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends Composite implements LoginView {

	private static LoginPanelUiBinder uiBinder = GWT.create(LoginPanelUiBinder.class);

	interface LoginPanelUiBinder extends UiBinder<Widget, LoginPanel> {}

	@UiField
	protected PaddedTextBox emailArea;

	@UiField
	protected Label messageLabel;

	@UiField
	protected PaddedPasswordTextBox passwordArea;

	@UiField
	protected Button loginButton;

	private final Presenter presenter;

	public LoginPanel(final LoginView.Presenter presenter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.presenter = presenter;
	}

	@UiHandler("loginButton")
	protected void onClick(final ClickEvent event) {
		doAuthenticate();
	}

	@UiHandler("passwordArea")
	protected void passwordAreaOnKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() != KEY_ENTER) return;
		doAuthenticate();
	}

	@UiHandler("emailArea")
	protected void onAttach(final AttachEvent event) {
		if (!event.isAttached()) return;
		emailArea.setFocus(true);
	}

	@UiHandler("emailArea")
	protected void emailAreaOnKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() != KEY_ENTER) return;
		doAuthenticate();
	}

	@Override
	public void setErrorMessage(final String message) {
		messageLabel.setText(message);
		messageLabel.setVisible(true);
	}

	private void doAuthenticate() {
		messageLabel.setVisible(false);
		presenter.onAuthenticationRequest(emailArea.getText(), passwordArea.getText());
	}

	@Override
	public void disable() {
		emailArea.setEnabled(false);
		passwordArea.setEnabled(false);
		loginButton.setEnabled(false);
	}

	@Override
	public void enable() {
		emailArea.setEnabled(true);
		passwordArea.setEnabled(true);
		loginButton.setEnabled(true);
	}
}
