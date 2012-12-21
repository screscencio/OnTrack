package br.com.oncast.ontrack.client.ui.components.members;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class DraggableMemberWidget extends Composite implements ModelWidget<UserRepresentation> {

	private static DraggableMemberWidgetUiBinder uiBinder = GWT.create(DraggableMemberWidgetUiBinder.class);

	interface DraggableMemberWidgetUiBinder extends UiBinder<Widget, DraggableMemberWidget> {}

	@UiField(provided = true)
	UserWidget userWidget;

	@UiField
	FocusPanel container;

	@UiField
	FocusPanel removeBtn;

	private final Timer hideRemoveOptionTimer = new Timer() {

		@Override
		public void run() {
			removeBtn.setVisible(false);
		}
	};

	private UserRepresentation user;

	private Scope association;

	public DraggableMemberWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		removeBtn.setVisible(false);
	}

	public DraggableMemberWidget(final UserRepresentation user) {
		this.user = user;
		this.userWidget = new UserWidget(user);
		initWidget(uiBinder.createAndBindUi(this));
		removeBtn.setVisible(false);
	}

	public DraggableMemberWidget(final UserRepresentation modelBean, final Scope scope) {
		this(modelBean);
		association = scope;
	}

	@Override
	public boolean update() {
		return false;
	}

	@Override
	public UserRepresentation getModelObject() {
		return user;
	}

	public Widget getDraggableItem() {
		return userWidget.getDraggableAnchor();
	}

	@UiHandler("container")
	protected void onUserMouseHover(final MouseMoveEvent event) {
		if (association == null) return;
		removeBtn.setVisible(true);
		hideRemoveOptionTimer.schedule(1500);
	}

	@UiHandler("removeBtn")
	protected void onRemoveClick(final ClickEvent event) {
		ClientServiceProvider.getInstance().getActionExecutionService()
				.onUserActionExecutionRequest(new ScopeRemoveAssociatedUserAction(association.getId(), user.getId()));
	}

	public void setAssociation(final Scope scope) {
		this.association = scope;
	}

	public void setSizeSmall() {
		userWidget.setSmallSize();
	}
}
