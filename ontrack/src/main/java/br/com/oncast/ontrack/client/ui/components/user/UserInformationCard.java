package br.com.oncast.ontrack.client.ui.components.user;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UserInformationCard extends Composite implements HasCloseHandlers<UserInformationCard>, PopupAware {

	private static UserInformationCardUiBinder uiBinder = GWT.create(UserInformationCardUiBinder.class);

	interface UserInformationCardUiBinder extends UiBinder<Widget, UserInformationCard> {}

	private static UserInformationCardMessages messages = GWT.create(UserInformationCardMessages.class);

	@UiField
	Image author;

	@UiField
	Label userEmail;

	@UiField(provided = true)
	EditableLabel userName;

	@UiField
	FocusPanel container;

	User user;

	public UserInformationCard() {
		userName = new EditableLabel(new EditableLabelEditionHandler() {

			@Override
			public boolean onEditionRequest(final String text) {
				if (text.isEmpty() || text.equals(user.getName())) return false;
				final User currentUser = ClientServiceProvider.getInstance().getAuthenticationService().getCurrentUser();
				if (!user.equals(currentUser)) return false;
				user.setName(text);

				ClientServiceProvider.getInstance().getUserDataService().onUserDataUpdate(user, new AsyncCallback<User>() {

					@Override
					public void onSuccess(final User result) {
						ClientServiceProvider.getInstance().getClientAlertingService().showSuccess(messages.userNameChangeSuccess());
					}

					@Override
					public void onFailure(final Throwable caught) {
						ClientServiceProvider.getInstance().getClientAlertingService().showError(messages.userNameChangeFailure());
					}
				});
				return true;
			}
		});

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void show() {}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<UserInformationCard> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	public void updateUser(final User user) {
		this.user = user;
		final User currentUser = ClientServiceProvider.getInstance().getAuthenticationService().getCurrentUser();
		userName.setReadOnly(!user.equals(currentUser));
		updateView();
	}

	private void updateView() {
		userEmail.setText(user.getEmail());
		userName.setValue(user.getName());
		author.setUrl(ClientServiceProvider.getInstance().getUserDataService().getAvatarUrl(user));
	}
}
