package br.com.oncast.ontrack.client.ui.places.planning.dnd;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.user.UserAssociationService;
import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.DraggableMembersListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.ModelWidgetContainerDragHandler;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.Widget;

public class UserAssociationDragHandler extends ModelWidgetContainerDragHandler<User> {

	private Scope previousScope;
	private DraggableMembersListWidget membersListWidget;
	private boolean hasCancelled = false;

	@Override
	public void onDragStart(final DragStartEvent event) {
		super.onDragStart(event);

		this.previousScope = null;
		this.membersListWidget = null;

		final Object source = event.getSource();
		if (!(source instanceof Widget)) return;

		Widget widget = (Widget) source;
		while (widget.getParent() != null) {
			if (widget instanceof ScopeWidget) {
				this.previousScope = ((ScopeWidget) widget).getModelObject();
				return;
			}
			else if (widget instanceof DraggableMembersListWidget) {
				this.membersListWidget = (DraggableMembersListWidget) widget;
				return;
			}
			widget = widget.getParent();
		}
	}

	@Override
	public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException {
		hasCancelled = false;

		final DropController dropController = event.getContext().finalDropController;
		if (dropController == null) return;

		hasCancelled = getUserAssociationService().hasAssociatedUser(getScope(dropController), getUser(event));
		if (hasCancelled) throw new VetoDragException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDragEnd(final DragEndEvent event) {
		if (hasCancelled) {
			super.onDragEnd(event);
			return;
		}
		if (membersListWidget != null) membersListWidget.update();

		removeFromPreviousScope(event);

		final DropController dropController = event.getContext().finalDropController;
		if (dropController == null) {
			event.getContext().draggable.removeFromParent();
			return;
		}

		super.addToCurrentContainer((ModelWidget<User>) event.getContext().draggable);
		getUserAssociationService().onAssociateUserRequest(getScope(dropController), getUser(event));

	}

	private void removeFromPreviousScope(final DragEndEvent event) {
		final DropController controller = event.getContext().finalDropController;
		if (previousScope == null || (controller != null && previousScope.equals(getScope(controller)))) return;

		getUserAssociationService().onUserRemoveAssociationRequest(previousScope, getUser(event));
	}

	@SuppressWarnings("unchecked")
	private Scope getScope(final DropController dropController) {
		return ((ModelWidget<Scope>) dropController.getDropTarget()).getModelObject();
	}

	@SuppressWarnings("unchecked")
	private UserRepresentation getUser(final DragEndEvent event) {
		return ((ModelWidget<UserRepresentation>) event.getContext().draggable).getModelObject();
	}

	private UserAssociationService getUserAssociationService() {
		return ClientServices.get().userAssociation();
	};

}
