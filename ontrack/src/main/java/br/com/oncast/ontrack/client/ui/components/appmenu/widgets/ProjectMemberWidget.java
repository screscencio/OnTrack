package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.user.PortableContactJsonObject;
import br.com.oncast.ontrack.client.services.user.UserDataService;
import br.com.oncast.ontrack.client.services.user.UserDataService.LoadProfileCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProjectMemberWidget extends Composite implements ModelWidget<User> {

	private static ProjectMemberWidgetUiBinder uiBinder = GWT.create(ProjectMemberWidgetUiBinder.class);

	interface ProjectMemberWidgetUiBinder extends UiBinder<Widget, ProjectMemberWidget> {}

	interface ProjectMemberWidgetStyle extends CssResource {
		String active();

		String inactive();
	}

	@UiField
	ProjectMemberWidgetStyle style;

	@UiField
	Label userName;

	@UiField
	HorizontalPanel container;

	@UiField
	Image userIcon;

	private User user;

	private UserStatus status;

	public ProjectMemberWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ProjectMemberWidget(final User modelBean, final UserStatus status) {
		this.user = modelBean;
		this.status = status;
		initWidget(uiBinder.createAndBindUi(this));
		container.setStyleName(status == UserStatus.ACTIVE ? style.active() : style.inactive());
		update();
	}

	@Override
	public boolean update() {
		final UserDataService userDataService = ClientServiceProvider.getInstance().getUserDataService();
		userIcon.setUrl(userDataService.getAvatarUrl(user.getEmail()));
		userIcon.setTitle(user.getEmail());

		userName.setText(user.getEmail());

		userDataService.loadProfile(user.getEmail(), new LoadProfileCallback() {

			@Override
			public void onProfileUnavailable(final Throwable cause) {}

			@Override
			public void onProfileLoaded(final PortableContactJsonObject profile) {
				userName.setText(profile.getDisplayName());
			}
		});
		return true;
	}

	@Override
	public User getModelObject() {
		return user;
	}
}
