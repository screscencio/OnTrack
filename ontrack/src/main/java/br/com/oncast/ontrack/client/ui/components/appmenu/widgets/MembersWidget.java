package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.context.ProjectAuthorizationCallback;
import br.com.oncast.ontrack.client.services.validation.EmailValidator;
import br.com.oncast.ontrack.client.ui.generalwidgets.DefaultTextedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.exceptions.authorization.PermissionDeniedException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.model.user.Profile;

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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

public class MembersWidget extends Composite implements HasCloseHandlers<MembersWidget>, PopupAware {

	private static final MembersWidgetMessages messages = GWT.create(MembersWidgetMessages.class);

	private static MembersWidgetUiBinder uiBinder = GWT.create(MembersWidgetUiBinder.class);

	private static ClientServices PROVIDER = ClientServices.get();

	interface MembersWidgetUiBinder extends UiBinder<Widget, MembersWidget> {}

	@UiField
	protected DefaultTextedTextBox invitationTextBox;

	@UiField
	protected CheckBox superUserCheck;

	public MembersWidget() {
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
	public HandlerRegistration addCloseHandler(final CloseHandler<MembersWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		focus();
		setDefaultText();
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
			protected void executeImpl(final MembersWidget widget) {
				final String mail = widget.invitationTextBox.getText();
				if (mail.trim().isEmpty() || !EmailValidator.isValid(mail)) return;
				widget.hide();
				ClientServices.get().alerting().showInfo(messages.processingYourInvitation());
				// FIXME change this for profile selection
				final Boolean isSuperUser = widget.superUserCheck.getValue();
				PROVIDER.projectRepresentationProvider().authorizeUser(mail, isSuperUser ? Profile.PROJECT_MANAGER : Profile.CONTRIBUTOR, new ProjectAuthorizationCallback() {
					@Override
					public void onSuccess() {
						ClientServices.get().alerting().showSuccess(messages.userInvited(mail));
					}

					@Override
					public void onFailure(final Throwable caught) {
						if (caught instanceof UnableToAuthorizeUserException) ClientServices.get().alerting().showWarning(messages.userAlreadyInvited(mail));
						if (caught instanceof PermissionDeniedException) ClientServices.get().alerting().showWarning(messages.permissionDenied());
						else {
							ClientServices.get().alerting().showError(caught.getMessage());
						}
					}
				});
			}
		},
		CANCEL(KEY_ESCAPE) {
			@Override
			protected void executeImpl(final MembersWidget widget) {
				widget.hide();
			}
		};

		private final int keyCode;

		private InvitationKeyDownHandler(final int keyCode) {
			this.keyCode = keyCode;
		}

		protected abstract void executeImpl(MembersWidget widget);

		static boolean handle(final MembersWidget widget, final KeyDownEvent event) {
			for (final InvitationKeyDownHandler handler : values()) {
				if (handler.keyCode == event.getNativeKeyCode()) {
					handler.execute(widget, event);
					return true;
				}
			}
			consume(event);
			return false;
		}

		private void execute(final MembersWidget widget, final DomEvent<?> event) {
			executeImpl(widget);
			consume(event);
		}

		private static void consume(final DomEvent<?> event) {
			event.stopPropagation();
		}

	}
}
