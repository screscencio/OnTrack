package br.com.oncast.ontrack.client.ui.components.members;

import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

	private UserRepresentation user;

	public DraggableMemberWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public DraggableMemberWidget(final UserRepresentation user) {
		this.user = user;
		this.userWidget = new UserWidget(user);
		initWidget(uiBinder.createAndBindUi(this));
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

}
