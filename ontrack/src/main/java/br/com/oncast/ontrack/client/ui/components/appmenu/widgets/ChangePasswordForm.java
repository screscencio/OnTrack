package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_TAB;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserPasswordChangeCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.HideHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.MaskPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

// FIXME Refactor widget name
public class ChangePasswordForm extends Composite implements HasCloseHandlers<ChangePasswordForm> {

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

	public ChangePasswordForm() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void hide() {
		if (!this.isVisible()) return;
		this.setVisible(false);
		CloseEvent.fire(this, this);
	}

	public void show() {
		clearFields();
		this.setVisible(true);

		MaskPanel.show(new HideHandler() {
			@Override
			public void onWillHide() {
				hide();
			}
		});
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
		else changeUserPassword();
	}

	// XXX Auth; Pre-process password (trim, etc) ?
	private void changeUserPassword() {
		ClientServiceProvider.getInstance().getAuthenticationService()
				.changePassword(oldPasswordArea.getText(), newPasswordArea.getText(), new UserPasswordChangeCallback() {

					@Override
					public void onUserPasswordChangedSuccessfully() {
						// TODO Improve feedback message.
						setInfoMessage("Password changed succefully.");
					}

					@Override
					public void onUnexpectedFailure(final Throwable caught) {
						// TODO Improve feedback message.
						setErrorMessage("Unexpected error.");

					}

					@Override
					public void onIncorrectUserPasswordFailure() {
						// TODO Improve feedback message.
						setErrorMessage("Incorrect old password.");
					}
				});

	}

	private boolean areTypedPasswordsEqual() {
		return newPasswordArea.getText().equals(retypePasswordArea.getText());
	}

	public void setErrorMessage(final String message) {
		messageLabel.setText(message);
	}

	public void setInfoMessage(final String message) {
		messageLabel.setText(message);
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

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ChangePasswordForm> handler) {
		return addHandler(handler, CloseEvent.getType());
	}
}
