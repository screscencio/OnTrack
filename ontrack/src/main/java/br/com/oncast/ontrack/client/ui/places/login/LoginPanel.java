package br.com.oncast.ontrack.client.ui.places.login;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import br.com.oncast.ontrack.client.ui.places.login.interaction.LoginRequestHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends Composite {

	private static LoginPanelUiBinder uiBinder = GWT.create(LoginPanelUiBinder.class);

	interface LoginPanelUiBinder extends UiBinder<Widget, LoginPanel> {}

	@UiField
	protected TextBox emailArea;

	@UiField
	protected HTMLPanel rootPanel;

	@UiField
	protected HTMLPanel informationPanel;

	@UiField
	protected Label messageLabel;

	@UiField
	protected PasswordTextBox passwordArea;

	@UiField
	protected Button loginButton;

	private final LoginRequestHandler authenticationRequestHandler;

	public LoginPanel(final LoginRequestHandler authenticationRequestHandler) {
		this.authenticationRequestHandler = authenticationRequestHandler;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("loginButton")
	protected void onClick(final ClickEvent event) {
		submitLogin();
	}

	@UiHandler("passwordArea")
	protected void passwordAreaOnKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() != KEY_ENTER) return;

		submitLogin();
	}

	@UiHandler("emailArea")
	protected void emailAreaOnKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() != KEY_ENTER) return;

		submitLogin();
	}

	public void setErrorMessage(final String message) {
		messageLabel.setText(message);
	}

	private boolean isEmailProvided() {
		if (emailArea.getText().trim().equals("")) {
			messageLabel.setText("Please provide your e-mail.");
			return false;
		}
		return true;
	}

	private void submitLogin() {
		if (!isEmailProvided()) return;

		authenticationRequestHandler.authenticateUser(this, emailArea.getText(), passwordArea.getText());
	}
}
