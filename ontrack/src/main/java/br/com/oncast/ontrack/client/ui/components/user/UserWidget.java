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

	private PopupConfig userCardPopUp;

	private final UserInformationCard userCard;

	private final UserRepresentation userRepresentation;

	private final Set<HandlerRegistration> registrationListener;

	private final boolean showActiveColor;

	private final UserUpdateListener updateListener;

	public UserWidget(final UserRepresentation userRepresentation) {
		this(userRepresentation, null);
	}

	public UserWidget(final UserRepresentation userRepresentation, final boolean showActiveColor) {
		this(userRepresentation, null, showActiveColor);
	}

	public UserWidget(final UserRepresentation userRepresentation, final UserUpdateListener updateListener) {
		this(userRepresentation, updateListener, true);
	}

	public UserWidget(final UserRepresentation userRepresentation, final UserUpdateListener updateListener, final boolean showActiveColor) {
		this.showActiveColor = showActiveColor;
		this.registrationListener = new HashSet<HandlerRegistration>();
		this.userRepresentation = userRepresentation;
		this.updateListener = updateListener;
		userCard = new UserInformationCard();
		initWidget(uiBinder.createAndBindUi(this));

		createUserInformationCard();
	}

	private void addHandlers() {
		if (!registrationListener.isEmpty()) return;

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
		addHandlers();
	}

	@Override
	protected void onDetach() {
		removeHandlers();
		super.onDetach();
	}

	private void removeHandlers() {
		for (final HandlerRegistration r : registrationListener)
			r.removeHandler();

		registrationListener.clear();
	}

	private void updateInfo(final User user) {
		final ClientServiceProvider provider = ClientServiceProvider.getInstance();

		userImage.setUrl(provider.getUserDataService().getAvatarUrl(user));
		userImage.setTitle(user.getName());
		userImage.setStyleName(style.showActiveColor(), showActiveColor);

		userCard.updateUser(user);
		if (updateListener != null) updateListener.onUserUpdate(user);
	}

	private void updateStatus(final UserStatus status) {
		userImage.setStyleName(style.offline(), status == UserStatus.OFFLINE);
		userImage.setStyleName(style.online(), status != UserStatus.OFFLINE);

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

	public Widget getDraggableAnchor() {
		return userImage;
	}

}
