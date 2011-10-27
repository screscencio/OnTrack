package br.com.oncast.ontrack.client.ui.places.planning.authentication;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_TAB;
import br.com.oncast.ontrack.client.ui.generalwidgets.MaskPanel;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningAuthenticationRequestHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
	protected FocusPanel clickableRootPanel;

	@UiField
	protected PasswordTextBox newPasswordArea;

	@UiField
	protected PasswordTextBox retypePasswordArea;

	@UiField
	protected Label messageLabel;

	@UiField
	protected Button changePasswordButton;

	private final MaskPanel maskPanel;

	private PlanningAuthenticationRequestHandler authenticationRequestHandler;

	public ChangePasswordForm() {
		initWidget(uiBinder.createAndBindUi(this));

		maskPanel = new MaskPanel();
		maskPanel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				hide();
			}
		});
	}

	public void hide() {
		if (!this.isVisible()) return;

		this.setVisible(false);
		maskPanel.hide();
	}

	public void show() {
		clearFields();
		this.setVisible(true);
		maskPanel.show();
	}

	public void focus() {
		oldPasswordArea.setFocus(true);
	}

	@UiHandler("oldPasswordArea")
	protected void oldPasswordAreaOnKeyUp(final KeyUpEvent event) {
		submitOrHideForm(event);
	}

	@UiHandler("changePasswordButton")
	protected void changePasswordButtonOnKeyDown(final KeyDownEvent event) {
		if (event.getNativeKeyCode() == KEY_TAB) {
			event.stopPropagation();
			event.preventDefault();
			oldPasswordArea.setFocus(true);
		}
	}

	@UiHandler("newPasswordArea")
	protected void newPasswordAreaOnKeyUp(final KeyUpEvent event) {
		submitOrHideForm(event);
	}

	@UiHandler("changePasswordButton")
	protected void changePasswordButtonOnKeyUp(final KeyUpEvent event) {
		submitOrHideForm(event);
	}

	@UiHandler("clickableRootPanel")
	protected void rootPanelOnKeyUp(final KeyUpEvent event) {
		submitOrHideForm(event);
	}

	@UiHandler("retypePasswordArea")
	protected void retypePasswordAreaOnKeyUp(final KeyUpEvent event) {
		submitOrHideForm(event);
	}

	@UiHandler("changePasswordButton")
	protected void changePasswordButtonOnClick(final ClickEvent e) {
		submitChangePassword();
	}

	private void submitChangePassword() {
		if (!areTypedPasswordsEqual()) messageLabel.setText("The two typed passwords are different.");
		else authenticationRequestHandler.changeUserPassword(this, oldPasswordArea.getText(), newPasswordArea.getText());
	}

	private boolean areTypedPasswordsEqual() {
		return newPasswordArea.getText().equals(retypePasswordArea.getText());
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

	private void clearFields() {
		oldPasswordArea.setText("");
		newPasswordArea.setText("");
		retypePasswordArea.setText("");
		messageLabel.setText("");
	}

	private void submitOrHideForm(final KeyUpEvent event) {
		if (event.getNativeKeyCode() == KEY_ENTER) submitChangePassword();
		if (event.getNativeKeyCode() == KEY_ESCAPE) this.hide();
	}
}
