package br.com.oncast.ontrack.client.ui.components.members;

import java.util.SortedSet;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.user.UserDataService;
import br.com.oncast.ontrack.client.services.user.UserStatus;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DraggableMemberWidget extends Composite implements ModelWidget<User> {

	private static final int ONLINE_OPACITY = 1;

	private static final double OFFLINE_OPACITY = 0.6;

	private static DraggableMemberWidgetUiBinder uiBinder = GWT.create(DraggableMemberWidgetUiBinder.class);

	interface DraggableMemberWidgetUiBinder extends UiBinder<Widget, DraggableMemberWidget> {}

	@UiField
	Image userIcon;

	@UiField
	FocusPanel container;

	private User user;

	private double opacity = OFFLINE_OPACITY;

	public DraggableMemberWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public DraggableMemberWidget(final User user) {
		this.user = user;
		initWidget(uiBinder.createAndBindUi(this));

		final ClientServiceProvider provider = ClientServiceProvider.getInstance();
		final UserDataService userDataService = provider.getUserDataService();

		userIcon.setUrl(userDataService.getAvatarUrl(user.getEmail()));
		userIcon.setTitle(user.getEmail());

		provider.getUsersStatusService().register(new UsersStatusChangeListener() {

			@Override
			public void onUsersStatusListsUpdated(final SortedSet<User> activeUsers, final SortedSet<User> onlineUsers) {
				final UserStatus status = activeUsers.contains(user) ? UserStatus.ACTIVE : onlineUsers.contains(user) ? UserStatus.ONLINE : UserStatus.OFFLINE;

				opacity = status != UserStatus.OFFLINE ? ONLINE_OPACITY : OFFLINE_OPACITY;
				container.getElement().getStyle().setOpacity(opacity);

				final String color = status == UserStatus.ACTIVE ? provider.getColorProviderService().getSelectionColorFor(user).toCssRepresentation() : "#888";

				userIcon.getElement().getStyle().setBorderColor(color);

			}

			@Override
			public void onUsersStatusListUnavailable(final Throwable caught) {

			}
		});
	}

	@Override
	public boolean update() {
		return false;
	}

	@Override
	public User getModelObject() {
		return user;
	}

	public Widget getDraggableItem() {
		return userIcon;
	}

	public double getOpacity() {
		return opacity;
	}

}
