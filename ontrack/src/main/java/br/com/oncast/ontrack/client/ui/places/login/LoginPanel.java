package br.com.oncast.ontrack.client.ui.places.login;

import br.com.oncast.ontrack.client.services.validation.EmailValidator;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ValidationInputContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ValidationInputContainer.ValidationHandler;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends Composite implements LoginView {

	private static LoginPanelUiBinder uiBinder = GWT.create(LoginPanelUiBinder.class);

	interface LoginPanelUiBinder extends UiBinder<Widget, LoginPanel> {}

	@UiField
	protected ValidationInputContainer emailArea;

	@UiField
	protected ValidationInputContainer passwordArea;

	@UiField
	protected Button loginButton;

	private final Presenter presenter;

	private boolean isEmailValid;

	public LoginPanel(final LoginView.Presenter presenter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.presenter = presenter;
		emailArea.setHandler(new ValidationHandler() {
			@Override
			public boolean isValid(final String email) {
				isEmailValid = !email.trim().isEmpty() && EmailValidator.isValid(email);
				return isEmailValid;
			}

			@Override
			public void onSubmit() {
				doAuthenticate();
			}
		});

		passwordArea.setHandler(new ValidationHandler() {
			@Override
			public boolean isValid(final String value) {
				return true;
			}

			@Override
			public void onSubmit() {
				doAuthenticate();
			}
		});
	}

	@UiHandler("loginButton")
	protected void onClick(final ClickEvent event) {
		doAuthenticate();
	}

	@UiHandler("emailArea")
	protected void onAttach(final AttachEvent event) {
		if (!event.isAttached()) return;
		emailArea.setFocus(true);
	}

	@UiHandler("emailArea")
	protected void onEmailTab(final KeyDownEvent event) {
		if (event.getNativeKeyCode() != BrowserKeyCodes.KEY_TAB) return;

		passwordArea.setFocus(true);
		event.preventDefault();
	}

	@UiHandler("passwordArea")
	protected void onPasswordTab(final KeyDownEvent event) {
		if (event.getNativeKeyCode() != BrowserKeyCodes.KEY_TAB) return;

		emailArea.setFocus(true);
		event.preventDefault();
	}

	private void doAuthenticate() {
		if (!isEmailValid) return;

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

	@Override
	public void onIncorrectCredentials() {
		emailArea.update(false);
		passwordArea.update(false);
	}
}
