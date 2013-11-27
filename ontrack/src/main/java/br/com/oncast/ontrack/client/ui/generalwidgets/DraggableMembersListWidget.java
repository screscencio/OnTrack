package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.user.UsersStatusService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.MembersWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.RemoveMembersWidget;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.SlideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.HideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ShowAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.SlideAndFadeAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.resources.client.CssResource;
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

	interface DraggableMemnbersListWidgetStyle extends CssResource {

		String hiddenButtons();

		String canInvite();
	}

	@UiField
	DraggableMemnbersListWidgetStyle style;

	@UiField
	HTMLPanel root;

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
		updateCanInvite();
	}

	@Override
	protected void onLoad() {
		if (!handlerRegistrations.isEmpty()) return;

		setupUsersStatusChangeListener();
		setupActionExecutionListener();
	}

	@Override
	protected void onUnload() {
		for (final HandlerRegistration reg : new HashSet<HandlerRegistration>(handlerRegistrations)) {
			reg.removeHandler();
		}
		handlerRegistrations.clear();
	}

	@UiHandler("addMember")
	protected void onAddMemberClick(final ClickEvent e) {
		PopupConfig.configPopup().popup(new MembersWidget()).alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(this, HorizontalAlignment.LEFT))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(this, VerticalAlignment.BOTTOM)).setAnimationDuration(SlideAnimation.DURATION_SHORT).pop();
	}

	@UiHandler("removeMember")
	protected void onRemoveMemberClick(final ClickEvent e) {
		PopupConfig.configPopup().popup(new RemoveMembersWidget()).alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(this, HorizontalAlignment.LEFT))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(this, VerticalAlignment.BOTTOM)).setAnimationDuration(SlideAnimation.DURATION_SHORT).pop();
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
		final UsersStatusService usersStatusService = ClientServices.get().usersStatus();
		updateMembersList(usersStatusService.getActiveUsers(), usersStatusService.getOnlineUsers());
		updateCanInvite();
	}

	private void updateCanInvite() {
		try {
			final UserRepresentation currentUser = ClientServices.getCurrentProjectContext().findUser(ClientServices.getCurrentUser());
			root.setStyleName(style.canInvite(), currentUser.canInvitePeople() && !currentUser.isReadOnly());
		} catch (final UserNotFoundException e) {
			root.setStyleName(style.canInvite(), false);
		}
	}

	private void updateMembersList(final SortedSet<UserRepresentation> activeUsers, final SortedSet<UserRepresentation> onlineUsers) {
		final ProjectContext currentProjectContext = ClientServices.getCurrentProjectContext();
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
		handlerRegistrations.add(ClientServices.get().actionExecution().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isUserAction) {
				if (execution.getModelAction() instanceof TeamAction) update();
			}

		}));
	}

	private void setupUsersStatusChangeListener() {
		handlerRegistrations.add(ClientServices.get().usersStatus().register(new UsersStatusChangeListener() {
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

	public void setButtonsVisibility(final boolean isHidden) {
		root.setStyleName(style.hiddenButtons(), !isHidden);
	}
}
