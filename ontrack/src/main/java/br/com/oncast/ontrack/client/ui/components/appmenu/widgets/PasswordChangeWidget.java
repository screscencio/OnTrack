package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_TAB;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserPasswordChangeCallback;
import br.com.oncast.ontrack.client.ui.components.appmenu.PasswordChangeWidgetMessages;
import br.com.oncast.ontrack.client.ui.generalwidgets.PaddedPasswordTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationPopupBoxContainer;
import br.com.oncast.ontrack.shared.utils.PasswordValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PasswordChangeWidget extends Composite implements HasCloseHandlers<PasswordChangeWidget>, PopupAware {

	private static final PasswordChangeWidgetMessages messages = GWT.create(PasswordChangeWidgetMessages.class);

	private static PasswordChangeWidgetUiBinder uiBinder = GWT.create(PasswordChangeWidgetUiBinder.class);

	interface PasswordChangeWidgetUiBinder extends UiBinder<Widget, PasswordChangeWidget> {}

	@UiField
	protected PaddedPasswordTextBox oldPasswordArea;

	@UiField
	protected ApplicationPopupBoxContainer clickableRootPanel;

	@UiField
	protected PaddedPasswordTextBox newPasswordArea;

	@UiField
	protected PaddedPasswordTextBox retypePasswordArea;

	@UiField
	protected Label messageLabel;

	@UiField
	protected Button changePasswordButton;

	public PasswordChangeWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		final KeyDownHandler consumeKeyDownHandler = new KeyDownHandler() {
			@Override
			public void onKeyDown(final KeyDownEvent event) {
				event.stopPropagation();
			}
		};
		oldPasswordArea.addKeyDownHandler(consumeKeyDownHandler);
		newPasswordArea.addKeyDownHandler(consumeKeyDownHandler);
		retypePasswordArea.addKeyDownHandler(consumeKeyDownHandler);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		CloseEvent.fire(this, this);
	}

	@Override
	public void show() {
		clearFields();
		focus();
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
		changePassword();
	}

	private void changePassword() {
		if (newPasswordArea.getText().isEmpty() || retypePasswordArea.getText().isEmpty()) showErrorMessage(messages.newPasswordCantBeEmpty());
		else if (!areTypedPasswordsEqual()) showErrorMessage(messages.passwordConfirmationFailed());
		else if (!PasswordValidator.isValid(newPasswordArea.getText())) showErrorMessage(messages.passwordMinCharRequirementFailed());
		else submitUserPasswordChange();
	}

	private void showErrorMessage(final String message) {
		messageLabel.setText(message);
		messageLabel.setVisible(true);
	}

	private void hideErrorMessage() {
		messageLabel.setText("");
		messageLabel.setVisible(false);
	}

	private void submitUserPasswordChange() {
		hideErrorMessage();
		disable();
		ClientServiceProvider.getInstance().getAuthenticationService()
				.changePassword(oldPasswordArea.getText(), newPasswordArea.getText(), new UserPasswordChangeCallback() {

					@Override
					public void onUserPasswordChangedSuccessfully() {
						ClientServiceProvider.getInstance().getClientNotificationService().showSuccess(messages.successfulChange());
						enable();
						hide();
					}

					@Override
					public void onUnexpectedFailure(final Throwable caught) {
						// TODO Improve feedback message.
						enable();
						ClientServiceProvider.getInstance().getClientNotificationService().showError(messages.unexpectedError());

					}

					@Override
					public void onIncorrectUserPasswordFailure() {
						// TODO Improve feedback message.
						enable();
						ClientServiceProvider.getInstance().getClientNotificationService().showError(messages.incorrectOldPassword());
					}
				});

	}

	private boolean areTypedPasswordsEqual() {
		return newPasswordArea.getText().equals(retypePasswordArea.getText());
	}

	private void clearFields() {
		oldPasswordArea.setText("");
		newPasswordArea.setText("");
		retypePasswordArea.setText("");
		hideErrorMessage();
	}

	private void submitOrHideForm(final KeyUpEvent event) {
		if (event.getNativeKeyCode() == KEY_ENTER) changePassword();
		if (event.getNativeKeyCode() == KEY_ESCAPE) this.hide();
		event.stopPropagation();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<PasswordChangeWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	protected void disable() {
		oldPasswordArea.setEnabled(false);
		newPasswordArea.setEnabled(false);
		retypePasswordArea.setEnabled(false);
		changePasswordButton.setEnabled(false);
	}

	protected void enable() {
		oldPasswordArea.setEnabled(true);
		newPasswordArea.setEnabled(true);
		retypePasswordArea.setEnabled(true);
		changePasswordButton.setEnabled(true);
	}
}
