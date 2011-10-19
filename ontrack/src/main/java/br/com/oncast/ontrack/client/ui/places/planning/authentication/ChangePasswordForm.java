package br.com.oncast.ontrack.client.ui.places.planning.authentication;

import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningAuthenticationRequestHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

public class ChangePasswordForm extends Composite {

	private static ChangePasswordFormUiBinder uiBinder = GWT.create(ChangePasswordFormUiBinder.class);

	interface ChangePasswordFormUiBinder extends UiBinder<Widget, ChangePasswordForm> {}

	@UiField
	protected PasswordTextBox oldPasswordArea;

	@UiField
	protected PasswordTextBox newPasswordArea;

	@UiField
	protected PasswordTextBox retypePasswordArea;

	@UiField
	protected Label messageLabel;

	@UiField
	protected FocusPanel rootPanel;

	@UiField
	protected Button changePasswordButton;

	@UiField
	protected Button closeButton;

	private PlanningAuthenticationRequestHandler authenticationRequestHandler;

	public ChangePasswordForm() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void hide() {
		if (!this.isVisible()) return;

		this.setVisible(false);
	}

	public void show() {
		this.setVisible(true);
	}

	public void focus() {
		oldPasswordArea.setFocus(true);
	}

	@UiHandler("closeButton")
	protected void closeButtonOnClick(final ClickEvent e) {
		this.hide();
	}

	@UiHandler("changePasswordButton")
	protected void changePasswordButtonOnClick(final ClickEvent e) {
		if (!areTypedPasswordsEqual()) return;

		authenticationRequestHandler.changeUserPassword(this, oldPasswordArea.getText(), newPasswordArea.getText());
	}

	private boolean areTypedPasswordsEqual() {
		if (!newPasswordArea.getText().equals(retypePasswordArea.getText())) {
			messageLabel.setText("The two typed passwords are different.");
			return false;
		}
		return true;
	}

	public void setAuthenticationRequestHandler(final PlanningAuthenticationRequestHandler authenticationRequestHandler) {
		this.authenticationRequestHandler = authenticationRequestHandler;
	}

	public void setErrorMessage(final String message) {
		messageLabel.setText(message);
		// FIXME Change css for this label
	}

	public void setInfoMessage(final String message) {
		messageLabel.setText(message);
		// FIXME Change css for this label
	}

}
