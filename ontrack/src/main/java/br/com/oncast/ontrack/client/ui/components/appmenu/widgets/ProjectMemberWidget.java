package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget.UserUpdateListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProjectMemberWidget extends Composite implements ModelWidget<UserRepresentation> {

	private static ProjectMemberWidgetUiBinder uiBinder = GWT.create(ProjectMemberWidgetUiBinder.class);

	interface ProjectMemberWidgetUiBinder extends UiBinder<Widget, ProjectMemberWidget> {}

	@UiField
	Label userName;

	@UiField
	HorizontalPanel container;

	@UiField(provided = true)
	UserWidget userWidget;

	private final UserRepresentation user;

	public ProjectMemberWidget(final UserRepresentation user) {
		this.user = user;
		userWidget = new UserWidget(user, new UserUpdateListener() {
			@Override
			public void onUserUpdate(final User user) {
				userName.setText(user.getName());
			}
		});
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
}
