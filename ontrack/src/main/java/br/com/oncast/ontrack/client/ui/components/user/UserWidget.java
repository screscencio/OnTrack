package br.com.oncast.ontrack.client.ui.components.user;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
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
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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

		String removed();

		String userImageContainerMedium();

		String userWithoutImageMedium();

		String userImageContainerSmall();

		String userWithoutImageSmall();

		String userImageContainerLabelColor();

		String userImageContainerImageColor();
	}

	@UiField
	UserWidgetStyle style;

	@UiField
	FocusPanel mask;

	@UiField
	DeckPanel userImageContainer;

	@UiField
	Label userWithoutImage;

	@UiField
	HTMLPanel userWithoutImageContainer;

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
		setMediumSize();
		showLabel();

		createUserInformationCard();

		addHandlers();

		updateRemoved();
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

		registrationListener.add(ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof TeamAction && action.getReferenceId().equals(userRepresentation.getId())) updateRemoved();
			}

		}));
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		addHandlers();
	}

	@Override
	protected void onUnload() {
		removeHandlers();
		super.onUnload();
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
		final ClientServiceProvider provider = ClientServiceProvider.getInstance();
		userWithoutImage.setText(user.getName().substring(0, 1));

		provider.getUserDataService().hasAvatarInGravatar(user, new UserHasGravatarCallback() {

			@Override
			public void onResponseReceived(final boolean hasGravatarAvatar) {
				if (hasGravatarAvatar) showImage();
				else showLabel();
			}
		});
		userImage.setUrl(provider.getUserDataService().getAvatarUrl(user));
		userImageContainer.setTitle(user.getName());
		userImage.setStyleName(style.showActiveColor(), showActiveColor);
		userWithoutImageContainer.setStyleName(style.showActiveColor(), showActiveColor);

		userCard.updateUser(user);
		if (updateListener != null) updateListener.onUserUpdate(user);
	}

	private void updateStatus(final UserStatus status) {
		userImage.setStyleName(style.offline(), status == UserStatus.OFFLINE);
		userImage.setStyleName(style.online(), status != UserStatus.OFFLINE);

		if (showActiveColor && status == UserStatus.ACTIVE) showColor();
	}

	private void showColor() {
		userImage.getElement().getStyle()
				.setBorderColor(COLOR_PROVIDER.getSelectionColorFor(userRepresentation).toCssRepresentation());
		userWithoutImageContainer.getElement().getStyle()
				.setBorderColor(COLOR_PROVIDER.getSelectionColorFor(userRepresentation).toCssRepresentation());
	}

	@UiHandler("mask")
	public void onAuthorClick(final ClickEvent event) {
		userCardPopUp.pop();
	}

	private void createUserInformationCard() {
		userCardPopUp = PopupConfig.configPopup()
				.popup(userCard)
				.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(userImageContainer.asWidget(), HorizontalAlignment.RIGHT, 10))
				.alignVertical(VerticalAlignment.MIDDLE, new AlignmentReference(userImageContainer.asWidget(), VerticalAlignment.BOTTOM, 15));

	}

	public void setMediumSize() {
		userImageContainer.setStyleName(style.userImageContainerSmall(), false);
		userWithoutImage.setStyleName(style.userWithoutImageSmall(), false);
		userImageContainer.setStyleName(style.userImageContainerMedium(), true);
		userWithoutImage.setStyleName(style.userWithoutImageMedium(), true);
	}

	public void setSmallSize() {
		userImageContainer.setStyleName(style.userImageContainerMedium(), false);
		userWithoutImage.setStyleName(style.userWithoutImageMedium(), false);
		userImageContainer.setStyleName(style.userImageContainerSmall(), true);
		userWithoutImage.setStyleName(style.userWithoutImageSmall(), true);
	}

	public interface UserUpdateListener {

		void onUserUpdate(User user);

	}

	public Widget getDraggableAnchor() {
		return mask;
	}

	public Widget getDraggableItem() {
		return userImageContainer;
	}

}
