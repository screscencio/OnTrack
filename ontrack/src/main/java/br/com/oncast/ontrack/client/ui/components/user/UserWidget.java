package br.com.oncast.ontrack.client.ui.components.user;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.user.ColorProviderService;
import br.com.oncast.ontrack.client.services.user.UserDataService;
import br.com.oncast.ontrack.client.services.user.UserDataServiceImpl.UserSpecificInformationChangeListener;
import br.com.oncast.ontrack.client.services.user.UserStatus;
import br.com.oncast.ontrack.client.services.user.UsersStatusService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UserSpecificStatusChangeListener;
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

	private static final UserDataService USER_DATA_SERVICE = ClientServiceProvider.getInstance().getUserDataService();

	private static final UsersStatusService USERS_STATUS_SERVICE = ClientServiceProvider.getInstance().getUsersStatusService();

	protected static final ColorProviderService COLOR_PROVIDER = ClientServiceProvider.getInstance().getColorProviderService();

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

	private final Set<HandlerRegistration> registrationListener;

	private boolean showActiveColor = true;

	private final UserUpdateListener updateListener;

	public UserWidget(final UserRepresentation userRepresentation) {
		this(userRepresentation, null);
	}

	public UserWidget(final UserRepresentation userRepresentation, final UserUpdateListener updateListener) {
		this.registrationListener = new HashSet<HandlerRegistration>();
		this.userRepresentation = userRepresentation;
		this.updateListener = updateListener;
		userCard = new UserInformationCard();
		initWidget(uiBinder.createAndBindUi(this));

		userImage.setUrl("https://secure.gravatar.com/avatar/5e443efe94a5ded46ed5b6b51505c768?s=40&d=mm");
		createUserInformationCard();
	}

	public UserWidget setShowActiveColor(final boolean b) {
		showActiveColor = b;
		return this;
	}

	private void createUserModificationListener() {
		registrationListener.add(USER_DATA_SERVICE.registerListenerForSpecificUser(userRepresentation, new UserSpecificInformationChangeListener() {
			@Override
			public void onInformationChange(final User user) {
				updateInfo(user);
			}
		}));

		registrationListener.add(USERS_STATUS_SERVICE.registerListenerForSpecificUser(userRepresentation, new UserSpecificStatusChangeListener() {
			@Override
			public void onUserStatusChange(final UserStatus status) {
				updateStatus(status);
			}

		}));
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		createUserModificationListener();
	}

	@Override
	protected void onDetach() {
		for (final HandlerRegistration r : registrationListener)
			r.removeHandler();

		super.onDetach();
	}

	private void updateInfo(final User user) {
		userCard.updateUser(user);
		final ClientServiceProvider provider = ClientServiceProvider.getInstance();

		userImage.setUrl(provider.getUserDataService().getAvatarUrl(user));
		userImage.setTitle(user.getName());

		userImage.setStyleName(style.showActiveColor(), showActiveColor);

		if (updateListener != null) updateListener.onUserUpdate(user);
	}

	private void updateStatus(final UserStatus status) {
		container.setStyleName(status == UserStatus.OFFLINE ? style.offline() : style.online());
		if (showActiveColor && status == UserStatus.ACTIVE) userImage.getElement().getStyle()
				.setBorderColor(COLOR_PROVIDER.getSelectionColorFor(userRepresentation).toCssRepresentation());
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

	public double getOpacity() {
		try {
			return Double.valueOf(container.getElement().getStyle().getOpacity());
		}
		catch (final Exception e) {
			return 1;
		}
	}

}
