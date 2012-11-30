package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeAssociatedMembersWidget extends Composite {

	private static final int VISIBLE_USERS_COUNT = 5;

	private static ScopeAssociatedMembersWidgetUiBinder uiBinder = GWT.create(ScopeAssociatedMembersWidgetUiBinder.class);

	interface ScopeAssociatedMembersWidgetUiBinder extends UiBinder<Widget, ScopeAssociatedMembersWidget> {}

	@UiField(provided = true)
	ModelWidgetContainer<UserRepresentation, DraggableMemberWidget> associatedUsers;

	private FlowPanel associatedUsersContainer;

	@UiField
	Label hiddenAssociatedUsersIndicator;

	private int visibleUsersCount = VISIBLE_USERS_COUNT;

	private final Scope scope;

	public ScopeAssociatedMembersWidget(final Scope scope, final DragAndDropManager userDragAndDropMananger) {
		this(scope, userDragAndDropMananger, VISIBLE_USERS_COUNT);
	}

	public ScopeAssociatedMembersWidget(final Scope scope, final DragAndDropManager userDragAndDropMananger, final int maxVisibleUsers) {
		this.scope = scope;
		this.visibleUsersCount = maxVisibleUsers;
		associatedUsers = createAssociatedUsersListWidget(userDragAndDropMananger);

		initWidget(uiBinder.createAndBindUi(this));

		update();
	}

	public void update() {
		if (scope.getProgress().isDone()) {
			this.setVisible(false);
			return;
		}

		final List<UserRepresentation> associatedUsersList = ClientServiceProvider.getInstance().getUserAssociationService().getAssociatedUsers(scope);
		associatedUsers.update(associatedUsersList);
		final int userCount = associatedUsersList.size();
		this.setVisible(userCount > 0);
		hiddenAssociatedUsersIndicator.setVisible(userCount > visibleUsersCount);
		hiddenAssociatedUsersIndicator.setText("+\n" + (userCount - visibleUsersCount));
		associatedUsersContainer.setWidth(Math.min(userCount, visibleUsersCount) * 36 + "px");
	}

	public void add(final DraggableMemberWidget memberWidget) {
		associatedUsers.getContainningPanel().add(memberWidget);
	}

	private ModelWidgetContainer<UserRepresentation, DraggableMemberWidget> createAssociatedUsersListWidget(final DragAndDropManager userDragAndDropMananger) {
		associatedUsersContainer = new FlowPanel();
		return new ModelWidgetContainer<UserRepresentation, DraggableMemberWidget>(new ModelWidgetFactory<UserRepresentation, DraggableMemberWidget>() {
			@Override
			public DraggableMemberWidget createWidget(final UserRepresentation modelBean) {
				final DraggableMemberWidget widget = new DraggableMemberWidget(modelBean);
				if (userDragAndDropMananger != null) userDragAndDropMananger.monitorNewDraggableItem(widget, widget.getDraggableItem());
				return widget;
			}
		}, new AnimatedContainer(associatedUsersContainer));
	}

}
