package br.com.oncast.ontrack.client.ui.components.user;

import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.user.UserDataUpdateListener;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.UserStatus;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class UserWidget extends Composite {

	private static UserWidgetUiBinder uiBinder = GWT.create(UserWidgetUiBinder.class);

	interface UserWidgetUiBinder extends UiBinder<Widget, UserWidget> {}

	interface UserWidgetStyle extends CssResource {
		String offline();

		String online();

		String showActiveColor();
	}

	@UiField
	UserWidgetStyle style;

	@UiField
	Image userImage;

	@UiField
	FocusPanel container;

	private PopupConfig userCardPopUp;

	private final UserInformationCard userCard;

	private final UserRepresentation userRepresentation;

	private HandlerRegistration registrationListener;

	private boolean showActiveColor = true;

	private final UserUpdateListener updateListener;

	public UserWidget(final UserRepresentation userRepresentation, final UserUpdateListener updateListener) {
		this.userRepresentation = userRepresentation;
		this.updateListener = updateListener;
		userCard = new UserInformationCard();
		initWidget(uiBinder.createAndBindUi(this));
		createUserInformationCard();
	}

	public UserWidget(final UserRepresentation userRepresentation) {
		this(userRepresentation, null);
	}

	public UserWidget setShowActiveColor(final boolean b) {
		showActiveColor = b;
		return this;
	}

	private void createUserModificationListener() {
		registrationListener = ClientServiceProvider.getInstance().getUserDataService()
				.addUserDataUpdateListener(new UserDataUpdateListener() {

					@Override
					public void onUserListLoaded(final List<User> users) {
						for (final User user : users) {
							if (user.getId().equals(userRepresentation.getId())) updateView(user);
						}
					}

					@Override
					public void onUserDataUpdate(final User user) {
						if (user.getId().equals(userRepresentation.getId())) updateView(user);
					}
				});
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		createUserModificationListener();
	}

	@Override
	protected void onDetach() {
		if (registrationListener != null) registrationListener.removeHandler();

		super.onDetach();
	}

	private void updateView(final User user) {
		userCard.updateUser(user);
		final ClientServiceProvider provider = ClientServiceProvider.getInstance();

		final UserStatus status = provider.getUsersStatusService().getStatus(userRepresentation);
		container.setStyleName(status == UserStatus.OFFLINE ? style.offline() : style.online());
		userImage.setUrl(provider.getUserDataService().getAvatarUrl(userRepresentation));
		userImage.setTitle(user.getName());

		userImage.setStyleName(style.showActiveColor(), showActiveColor);
		if (showActiveColor && status == UserStatus.ACTIVE) userImage.getElement().getStyle()
				.setBorderColor(provider.getColorProviderService().getSelectionColorFor(userRepresentation).toCssRepresentation());

		if (updateListener != null) updateListener.onUserUpdate(user);
	}

	@UiHandler("userImage")
	public void onAuthorClick(final ClickEvent event) {
		userCardPopUp.pop();
	}

	private void createUserInformationCard() {
		userCardPopUp = PopupConfig.configPopup()
				.popup(userCard)
				.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(userImage.asWidget(), HorizontalAlignment.RIGHT, 10))
				.alignVertical(VerticalAlignment.MIDDLE, new AlignmentReference(userImage.asWidget(), VerticalAlignment.BOTTOM, 15));

	}

	public interface UserUpdateListener {

		void onUserUpdate(User user);

	}

}
