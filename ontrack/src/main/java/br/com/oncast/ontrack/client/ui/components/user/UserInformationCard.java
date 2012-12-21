package br.com.oncast.ontrack.client.ui.components.user;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.user.UserHasGravatarCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UserInformationCard extends Composite implements HasCloseHandlers<UserInformationCard>, PopupAware {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static UserInformationCardUiBinder uiBinder = GWT.create(UserInformationCardUiBinder.class);

	interface UserInformationCardUiBinder extends UiBinder<Widget, UserInformationCard> {}

	private static UserInformationCardMessages messages = GWT.create(UserInformationCardMessages.class);

	interface UserInformationCardStyle extends CssResource {

		String userImageContainerImageColor();

		String userImageContainerLabelColor();
	}

	@UiField
	UserInformationCardStyle style;

	@UiField
	Image author;

	@UiField
	Label userEmail;

	@UiField(provided = true)
	EditableLabel userName;

	@UiField
	FocusPanel container;

	@UiField
	DeckPanel userImageContainer;

	@UiField
	Label userWithoutImage;

	private User user;

	public UserInformationCard() {
		userName = new EditableLabel(new EditableLabelEditionHandler() {

			@Override
			public boolean onEditionRequest(final String text) {
				if (text.isEmpty() || text.equals(user.getName())) return false;
				if (!user.equals(ClientServiceProvider.getCurrentUser())) return false;
				user.setName(text);

				SERVICE_PROVIDER.getUserDataService().onUserDataUpdate(user, new AsyncCallback<User>() {

					@Override
					public void onSuccess(final User result) {
						SERVICE_PROVIDER.getClientAlertingService().showSuccess(messages.userNameChangeSuccess());
					}

					@Override
					public void onFailure(final Throwable caught) {
						SERVICE_PROVIDER.getClientAlertingService().showError(messages.userNameChangeFailure());
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
		userName.setReadOnly(!user.equals(ClientServiceProvider.getCurrentUser()));
		updateView();
	}

	private void showLabel() {
		userImageContainer.setStyleName(style.userImageContainerImageColor(), false);
		userImageContainer.setStyleName(style.userImageContainerLabelColor(), true);
		userImageContainer.showWidget(1);
	}

	private void showImage() {
		userImageContainer.setStyleName(style.userImageContainerLabelColor(), false);
		userImageContainer.setStyleName(style.userImageContainerImageColor(), true);
		userImageContainer.showWidget(0);
	}

	private void updateView() {
		userEmail.setText(user.getEmail());
		userName.setValue(user.getName());
		userWithoutImage.setText(user.getName().substring(0, 1));
		author.setUrl(SERVICE_PROVIDER.getUserDataService().getAvatarUrl(user));

		SERVICE_PROVIDER.getUserDataService().hasAvatarInGravatar(user, new UserHasGravatarCallback() {

			@Override
			public void onResponseReceived(final boolean hasGravatarAvatar) {
				if (hasGravatarAvatar) showImage();
				else showLabel();
			}
		});

		userImageContainer.showWidget(1);
	}
}
