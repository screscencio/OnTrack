package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.user.UsersStatusService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.HideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ShowAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.SlideAndFadeAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class DraggableMembersListWidget extends Composite {

	private static DraggableMembersListWidgetUiBinder uiBinder = GWT.create(DraggableMembersListWidgetUiBinder.class);

	interface DraggableMembersListWidgetUiBinder extends UiBinder<Widget, DraggableMembersListWidget> {}

	@UiField(provided = true)
	ModelWidgetContainer<UserRepresentation, DraggableMemberWidget> users;

	@UiField
	HTMLPanel scroll;

	private final HorizontalScrollMover mover;

	private final DragAndDropManager userDragAndDropManager;

	private final Set<HandlerRegistration> handlerRegistrations = new HashSet<HandlerRegistration>();

	public DraggableMembersListWidget(final DragAndDropManager userDragAndDropManager) {
		this.userDragAndDropManager = userDragAndDropManager;
		users = createModelWidgetContainer();
		initWidget(uiBinder.createAndBindUi(this));

		mover = new HorizontalScrollMover(scroll.getElement());
	}

	@Override
	protected void onLoad() {
		setupUsersStatusChangeListener();
		setupActionExecutionListener();
	}

	@Override
	protected void onUnload() {
		for (final HandlerRegistration reg : handlerRegistrations) {
			reg.removeHandler();
		}
	}

	@UiHandler("right")
	protected void onRightMouseOver(final MouseOverEvent e) {
		mover.moveRight();
	}

	@UiHandler("right")
	protected void onRightMouseOut(final MouseOutEvent e) {
		mover.cancel();
	}

	@UiHandler("left")
	protected void onLeftMouseOver(final MouseOverEvent e) {
		mover.moveLeft();
	}

	@UiHandler("left")
	protected void onLeftMouseOut(final MouseOutEvent e) {
		mover.cancel();
	}

	public void update() {
		final UsersStatusService usersStatusService = ClientServiceProvider.getInstance().getUsersStatusService();
		updateMembersList(usersStatusService.getActiveUsers(), usersStatusService.getOnlineUsers());
	}

	private void updateMembersList(final SortedSet<UserRepresentation> activeUsers, final SortedSet<UserRepresentation> onlineUsers) {
		final ProjectContext currentProjectContext = ClientServiceProvider.getCurrentProjectContext();
		final ArrayList<UserRepresentation> userModels = new ArrayList<UserRepresentation>(activeUsers);

		for (final UserRepresentation user : onlineUsers) {
			if (!userModels.contains(user)) userModels.add(user);
		}
		for (final UserRepresentation user : currentProjectContext.getUsers()) {
			if (!userModels.contains(user) && user.isValid()) userModels.add(user);
		}

		this.users.update(userModels);
	}

	private void setupActionExecutionListener() {
		handlerRegistrations.add(ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof TeamAction) {
					update();
				}
			}

		}));
	}

	private void setupUsersStatusChangeListener() {
		handlerRegistrations.add(ClientServiceProvider.getInstance().getUsersStatusService().register(new UsersStatusChangeListener() {
			@Override
			public void onUsersStatusListUnavailable(final Throwable caught) {
				updateMembersList(new TreeSet<UserRepresentation>(), new TreeSet<UserRepresentation>());
			}

			@Override
			public void onUsersStatusListsUpdated(final SortedSet<UserRepresentation> activeUsers, final SortedSet<UserRepresentation> onlineUsers) {
				updateMembersList(activeUsers, onlineUsers);
			}
		}));
	}

	private ModelWidgetContainer<UserRepresentation, DraggableMemberWidget> createModelWidgetContainer() {
		return new ModelWidgetContainer<UserRepresentation, DraggableMemberWidget>(new ModelWidgetFactory<UserRepresentation, DraggableMemberWidget>() {
			@Override
			public DraggableMemberWidget createWidget(final UserRepresentation modelBean) {
				final DraggableMemberWidget widget = new DraggableMemberWidget(modelBean);
				userDragAndDropManager.monitorNewDraggableItem(widget, widget.getDraggableItem());
				return widget;
			}
		}, new AnimatedContainer(new HorizontalPanel(), new AnimationFactory() {

			@Override
			public ShowAnimation createShowAnimation(final Widget widget) {
				return new SlideAndFadeAnimation(widget, true);
			}

			@Override
			public HideAnimation createHideAnimation(final Widget widget) {
				return new SlideAndFadeAnimation(widget, true);
			}
		}));
	}
}
