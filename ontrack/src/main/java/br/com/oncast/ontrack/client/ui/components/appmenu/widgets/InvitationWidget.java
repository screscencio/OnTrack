package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectAuthorizationCallback;
import br.com.oncast.ontrack.client.services.validation.EmailValidator;
import br.com.oncast.ontrack.client.ui.generalwidgets.DefaultTextedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class InvitationWidget extends Composite implements HasCloseHandlers<InvitationWidget>, PopupAware {

	private static InvitationWidgetUiBinder uiBinder = GWT.create(InvitationWidgetUiBinder.class);

	private static ClientServiceProvider PROVIDER = ClientServiceProvider.getInstance();

	interface InvitationWidgetUiBinder extends UiBinder<Widget, InvitationWidget> {}

	@UiField
	protected DefaultTextedTextBox invitationTextBox;

	@UiField
	protected Label countdownLabel;

	public InvitationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("invitationTextBox")
	protected void onKeyDown(final KeyDownEvent event) {
		InvitationKeyDownHandler.handle(this, event);
	}

	@UiHandler("invitationTextBox")
	protected void onKeyUp(final KeyUpEvent event) {
		if (invitationTextBox.getText().isEmpty()) setDefaultText();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<InvitationWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		focus();
		setDefaultText();
		validateCountdown();
	}

	private void validateCountdown() {
		final User currentUser = ClientServiceProvider.getInstance().getAuthenticationService().getCurrentUser();
		final int invitationQuota = (currentUser == null || currentUser.getProjectInvitationQuota() <= 0) ? 0 : currentUser.getProjectInvitationQuota();

		countdownLabel.setText("You have '" + invitationQuota + "' invitations left.");
		invitationTextBox.setEnabled(invitationQuota > 0);
	}

	private void setDefaultText() {
		invitationTextBox.setCursorPos(0);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		invitationTextBox.setText("");
		CloseEvent.fire(this, this);
	}

	public void focus() {
		invitationTextBox.setFocus(true);
		invitationTextBox.selectAll();
	}

	private enum InvitationKeyDownHandler {

		INVITE(KEY_ENTER) {
			@Override
			protected void executeImpl(final InvitationWidget widget) {
				final String mail = widget.invitationTextBox.getText();
				if (mail.trim().isEmpty() || !EmailValidator.isValid(mail)) return;

				widget.hide();
				ClientServiceProvider.getInstance().getClientNotificationService().showInfo("Processing you invitation...");
				PROVIDER.getProjectRepresentationProvider().authorizeUser(mail, new ProjectAuthorizationCallback() {
					@Override
					public void onSuccess() {
						ClientServiceProvider.getInstance().getClientNotificationService()
								.showSuccess("'" + mail + "' was invited!");
					}

					@Override
					public void onFailure(final Throwable caught) {
						if (caught instanceof UnableToAuthorizeUserException) ClientServiceProvider.getInstance().getClientNotificationService()
								.showWarning("'" + mail + "' already has been invited");
						else
						ClientServiceProvider.getInstance().getClientNotificationService().showWarning(caught.getMessage());
					}
				});
			}
		},
		CANCEL(KEY_ESCAPE) {
			@Override
			protected void executeImpl(final InvitationWidget widget) {
				widget.hide();
			}
		};

		private final int keyCode;

		private InvitationKeyDownHandler(final int keyCode) {
			this.keyCode = keyCode;
		}

		protected abstract void executeImpl(InvitationWidget widget);

		static boolean handle(final InvitationWidget widget, final KeyDownEvent event) {
			for (final InvitationKeyDownHandler handler : values()) {
				if (handler.keyCode == event.getNativeKeyCode()) {
					handler.execute(widget, event);
					return true;
				}
			}
			consume(event);
			return false;
		}

		private void execute(final InvitationWidget widget, final DomEvent<?> event) {
			executeImpl(widget);
			consume(event);
		}

		private static void consume(final DomEvent<?> event) {
			event.stopPropagation();
		}

	}
}
