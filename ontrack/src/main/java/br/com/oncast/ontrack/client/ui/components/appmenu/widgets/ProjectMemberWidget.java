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
		String offline();

		String online();
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

	public ProjectMemberWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ProjectMemberWidget(final User user, final UserStatus status) {
		this.user = user;
		initWidget(uiBinder.createAndBindUi(this));

		final ClientServiceProvider provider = ClientServiceProvider.getInstance();
		final UserDataService userDataService = provider.getUserDataService();

		container.setStyleName(status == UserStatus.OFFLINE ? style.offline() : style.online());

		if (status == UserStatus.ACTIVE) userIcon.getElement().getStyle()
				.setBorderColor(provider.getMembersScopeSelectionService().getSelectionColor(user));
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
	}

	@Override
	public boolean update() {
		return false;
	}

	@Override
	public User getModelObject() {
		return user;
	}
}
