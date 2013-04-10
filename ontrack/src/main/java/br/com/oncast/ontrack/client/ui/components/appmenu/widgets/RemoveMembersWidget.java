package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectAuthorizationCallback;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.DraggableMembersListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.ModelWidgetContainerDragHandler;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class RemoveMembersWidget extends Composite implements HasCloseHandlers<RemoveMembersWidget>, PopupAware {

	private static final RemoveMembersWidgetMessages messages = GWT.create(RemoveMembersWidgetMessages.class);

	private static MembersWidgetUiBinder uiBinder = GWT.create(MembersWidgetUiBinder.class);

	interface MembersWidgetUiBinder extends UiBinder<Widget, RemoveMembersWidget> {}

	interface MembersWidgetStyle extends CssResource {
		String dropTargetActive();
	}

	@UiField
	MembersWidgetStyle style;

	@UiField(provided = true)
	protected DraggableMembersListWidget members;

	@UiField
	protected FocusPanel dropTarget;

	public RemoveMembersWidget() {
		final DragAndDropManager manager = new DragAndDropManager();
		manager.configureBoundaryPanel(RootPanel.get());
		members = new DraggableMembersListWidget(manager);
		members.setButtonsVisibility(false);
		initWidget(uiBinder.createAndBindUi(this));

		manager.addDragHandler(new ModelWidgetContainerDragHandler<UserRepresentation>() {
			@Override
			public void onDragEnd(final DragEndEvent event) {
				super.onDragEnd(event);
				members.update();
			}
		});
		manager.monitorDropTarget(dropTarget, new DropControllerFactory() {
			@Override
			public DropController create(final Widget panel) {
				return new RemoveMembersDropController(panel);
			}
		});
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<RemoveMembersWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		members.setVisible(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		CloseEvent.fire(this, this);
	}

	private class RemoveMembersDropController extends SimpleDropController {

		public RemoveMembersDropController(final Widget dropTarget) {
			super(dropTarget);
		}

		@Override
		public void onEnter(final DragContext context) {
			super.onEnter(context);
			dropTarget.setStyleName(style.dropTargetActive(), true);
		}

		@Override
		public void onLeave(final DragContext context) {
			super.onLeave(context);
			dropTarget.setStyleName(style.dropTargetActive(), false);
		}

		@Override
		public void onDrop(final DragContext context) {
			super.onDrop(context);
			removeUser(((DraggableMemberWidget) context.draggable).getModelObject());
		}

		private void removeUser(final UserRepresentation user) {
			ClientServiceProvider.get().projectRepresentationProvider().unauthorizeUser(user, new ProjectAuthorizationCallback() {
				@Override
				public void onSuccess() {
					ClientServiceProvider.get().alerting()
							.showSuccess(messages.userRemoved());
				}

				@Override
				public void onFailure(final Throwable caught) {
					final boolean knownError = caught instanceof UnableToRemoveAuthorizationException;
					if (!knownError) caught.printStackTrace();

					ClientServiceProvider.get().alerting()
							.showError(knownError ? messages.cantRemoveAdmin() : messages.userRemoveFailed());
				}
			});
		}

	}

}
