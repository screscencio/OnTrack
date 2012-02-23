package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_TAB;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectAuthorizationCallback;
import br.com.oncast.ontrack.client.services.messages.ClientNotificationService;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InvitationWidget extends Composite implements HasCloseHandlers<InvitationWidget>, PopupAware {

	private static final String DEFAULT_TEXT = "mail@domain.com";

	private static InvitationWidgetUiBinder uiBinder = GWT.create(InvitationWidgetUiBinder.class);

	private static ClientServiceProvider PROVIDER = ClientServiceProvider.getInstance();

	interface InvitationWidgetUiBinder extends UiBinder<Widget, InvitationWidget> {}

	@UiField
	protected TextBox invitationTextBox;

	@UiField
	protected Button inviteButton;

	public InvitationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("invitationTextBox")
	protected void onKeyDown(final KeyDownEvent event) {
		InvitationKeyDownHandler.handle(this, event);
	}

	@UiHandler("inviteButton")
	protected void inviteButtonClick(final ClickEvent event) {
		InvitationKeyDownHandler.INVITE.execute(this, event);
	}

	@UiHandler("inviteButton")
	protected void inviteButtonOnKeyDown(final KeyDownEvent event) {
		if (event.getNativeKeyCode() == KEY_TAB) {
			event.stopPropagation();
			event.preventDefault();
			invitationTextBox.setFocus(true);
		}
		else {
			InvitationKeyDownHandler.handle(this, event);
		}
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<InvitationWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		this.setVisible(true);
		focus();
		setDefaultText();
	}

	private void setDefaultText() {
		invitationTextBox.setText(DEFAULT_TEXT);
		invitationTextBox.setSelectionRange(0, DEFAULT_TEXT.length());
	}

	@Override
	public void hide() {
		this.setVisible(false);
		invitationTextBox.setText("");
		CloseEvent.fire(this, this);
	}

	public void focus() {
		invitationTextBox.setFocus(true);
	}

	private enum InvitationKeyDownHandler {

		INVITE(KEY_ENTER, true) {
			@Override
			protected void executeImpl(final InvitationWidget widget) {
				final String mail = widget.invitationTextBox.getText();
				widget.hide();
				// FIXME Mats change this for show info or waiting message;
				ClientNotificationService.showSuccess("Processing you invitation in background...");
				PROVIDER.getProjectRepresentationProvider().authorizeUser(mail, new ProjectAuthorizationCallback() {
					@Override
					public void onSuccess() {
						ClientNotificationService.showSuccess("User with e-mail '" + mail + "' was successfully invited");
					}

					@Override
					public void onFailure(final Throwable caught) {
						ClientNotificationService.showError(caught.getMessage());
					}
				});
			}
		},
		CANCEL(KEY_ESCAPE, true) {
			@Override
			protected void executeImpl(final InvitationWidget widget) {
				widget.hide();
			}
		};

		private final int keyCode;
		private boolean shouldConsume;

		private InvitationKeyDownHandler(final int keyCode, final boolean shouldConsume) {
			this.keyCode = keyCode;
			this.shouldConsume = shouldConsume;
		}

		protected abstract void executeImpl(InvitationWidget widget);

		static void handle(final InvitationWidget widget, final KeyDownEvent event) {
			for (final InvitationKeyDownHandler handler : values()) {
				if (handler.keyCode == event.getNativeKeyCode()) {
					handler.execute(widget, event);
					return;
				}
			}
		}

		private void execute(final InvitationWidget widget, final DomEvent<?> event) {
			executeImpl(widget);
			consume(event);
		}

		private void consume(final DomEvent<?> event) {
			if (shouldConsume) {
				event.preventDefault();
				event.stopPropagation();
			}

		}

	}
}
