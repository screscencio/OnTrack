package br.com.oncast.ontrack.client.ui.components.user;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.user.ColorProviderService;
import br.com.oncast.ontrack.client.services.user.UserDataService;
import br.com.oncast.ontrack.client.services.user.UserDataServiceImpl.UserSpecificInformationChangeListener;
import br.com.oncast.ontrack.client.services.user.UserHasGravatarCallback;
import br.com.oncast.ontrack.client.services.user.UserStatus;
import br.com.oncast.ontrack.client.services.user.UsersStatusService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UserSpecificStatusChangeListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UserWidget extends Composite {

	private static final UserDataService USER_DATA_SERVICE = ClientServices.get().userData();

	private static final UsersStatusService USERS_STATUS_SERVICE = ClientServices.get().usersStatus();

	protected static final ColorProviderService COLOR_PROVIDER = ClientServices.get().colorProvider();

	private static UserWidgetUiBinder uiBinder = GWT.create(UserWidgetUiBinder.class);

	interface UserWidgetUiBinder extends UiBinder<Widget, UserWidget> {}

	interface UserWidgetStyle extends CssResource {
		String offline();

		String online();

		String removed();

		String userImageContainerMedium();

		String userImageContainerSmall();
	}

	@UiField
	UserWidgetStyle style;

	@UiField
	FocusPanel activeIndicator;

	@UiField
	FocusPanel mask;

	@UiField
	DeckPanel userImageContainer;

	@UiField
	Label userWithoutImage;

	@UiField
	Image userImage;

	private final Set<HandlerRegistration> registrationListener;

	private final UserRepresentation userRepresentation;

	private User user;

	private UserUpdateListener updateListener;

	private PopupConfig userCardPopUp;

	private UserInformationCard userCard;

	public UserWidget(final UUID userId) {
		this(findUserOrCreateFakeOne(userId));
	}

	public UserWidget(final UserRepresentation userRepresentation) {
		this.userRepresentation = userRepresentation;
		this.registrationListener = new HashSet<HandlerRegistration>();
		initWidget(uiBinder.createAndBindUi(this));

		showLabel();
		updateRemoved();
	}

	public UserWidget setUpdateListener(final UserUpdateListener updateListener) {
		this.updateListener = updateListener;
		if (user != null) updateListener.onUserUpdate(user);
		return this;
	}

	public UserWidget showUserStatus(final boolean visible) {
		activeIndicator.setVisible(visible);
		return this;
	}

	public UserWidget setMediumSize() {
		userImageContainer.setStyleName(style.userImageContainerMedium());
		return this;
	}

	public UserWidget setSmallSize() {
		userImageContainer.setStyleName(style.userImageContainerSmall());
		return this;
	}

	public Widget getDraggableAnchor() {
		return mask;
	}

	public Widget getDraggableItem() {
		return userImageContainer;
	}

	@UiHandler("mask")
	public void onAuthorClick(final ClickEvent event) {
		getUserCardPopUp().pop();
	}

	private void showLabel() {
		userImageContainer.showWidget(1);
	}

	private void showImage() {
		userImageContainer.showWidget(0);
	}

	@Override
	protected void onLoad() {
		addHandlers();
	}

	@Override
	protected void onUnload() {
		removeHandlers();
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

		registrationListener.add(ClientServices.get().actionExecution().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final ActionExecutionContext executionContext,
					final boolean isUserAction) {
				if (action instanceof TeamAction && action.getReferenceId().equals(userRepresentation.getId())) updateRemoved();
			}

		}));
	}

	private void removeHandlers() {
		for (final HandlerRegistration r : registrationListener)
			r.removeHandler();

		registrationListener.clear();
	}

	private void updateRemoved() {
		mask.setStyleName(style.removed(), !userRepresentation.isValid());
	}

	private void updateInfo(final User user) {
		this.user = user;
		final ClientServices provider = ClientServices.get();
		userWithoutImage.setText(user.getName().substring(0, 1));

		provider.userData().hasAvatarInGravatar(user, new UserHasGravatarCallback() {

			@Override
			public void onResponseReceived(final boolean hasGravatarAvatar) {
				if (hasGravatarAvatar) showImage();
				else showLabel();
			}
		});
		userImage.setUrl(provider.userData().getAvatarUrl(user));
		userImageContainer.setTitle(user.getName());

		getUserCard().updateUser(user);
		if (updateListener != null) updateListener.onUserUpdate(user);
	}

	private void updateStatus(final UserStatus status) {
		userImageContainer.setStyleName(style.offline(), status == UserStatus.OFFLINE);
		userImageContainer.setStyleName(style.online(), status != UserStatus.OFFLINE);

		updateUserActiveColor(status);
	}

	private void updateUserActiveColor(final UserStatus status) {
		final Style s = activeIndicator.getElement().getStyle();
		if (status == UserStatus.ACTIVE) s.setBackgroundColor(COLOR_PROVIDER.getSelectionColorFor(userRepresentation).toCssRepresentation());
		else s.clearBackgroundColor();
	}

	private UserInformationCard getUserCard() {
		return userCard == null ? userCard = new UserInformationCard(userRepresentation) : userCard;
	}

	private PopupConfig getUserCardPopUp() {
		if (userCardPopUp == null) {
			userCardPopUp = PopupConfig.configPopup().popup(getUserCard())
					.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(userImageContainer.asWidget(), HorizontalAlignment.RIGHT, 10))
					.alignVertical(VerticalAlignment.MIDDLE, new AlignmentReference(userImageContainer.asWidget(), VerticalAlignment.BOTTOM, 15));
		}

		return userCardPopUp;
	}

	// FIXME Refactor this to remove the need of creating fake UserRepresentation
	private static UserRepresentation findUserOrCreateFakeOne(final UUID userId) {
		try {
			if (ClientServices.get().contextProvider().isContextAvailable()) return ClientServices.getCurrentProjectContext().findUser(userId);
		} catch (final UserNotFoundException e) {
			ClientServices.get().metrics().onException("findUserOrCreateFakeOne(" + userId + "): User not found in the context");
		}
		ClientServices.get().metrics().onException("findUserOrCreateFakeOne(" + userId + "): Context unavailable");
		return new UserRepresentation(userId);
	}

	public interface UserUpdateListener {

		void onUserUpdate(User user);

	}

}
