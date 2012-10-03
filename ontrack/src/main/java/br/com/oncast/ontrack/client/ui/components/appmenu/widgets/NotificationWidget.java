package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.user.PortableContactJsonObject;
import br.com.oncast.ontrack.client.services.user.UserDataService;
import br.com.oncast.ontrack.client.services.user.UserDataService.LoadProfileCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NotificationWidget extends Composite implements ModelWidget<Notification> {

	private static final NotificationWidgetMessages messages = GWT.create(NotificationWidgetMessages.class);

	private static NotificationWidgetUiBinder uiBinder = GWT.create(NotificationWidgetUiBinder.class);

	interface NotificationWidgetUiBinder extends UiBinder<Widget, NotificationWidget> {}

	@UiField
	protected Label projectName;

	@UiField
	protected Label type;

	@UiField
	protected Label userName;

	@UiField
	protected Label timestamp;

	@UiField
	Image userIcon;

	private final Notification notification;

	public NotificationWidget(final Notification modelBean) {
		this.notification = modelBean;

		initWidget(uiBinder.createAndBindUi(this));
		setVisible(false);

		update();

		setVisible(true);
	}

	@Override
	public boolean update() {

		fillUserInformation();

		final ProjectRepresentationProvider projectRepresentationProvider = ClientServiceProvider.getInstance().getProjectRepresentationProvider();
		projectName.setText(projectRepresentationProvider.getProjectRepresentation(notification.getProjectId()).getName());

		type.setText(notification.getType().toString());
		timestamp.setText(HumanDateFormatter.getDifferenceDate(notification.getTimestamp()));
		return true;
	}

	@Override
	public Notification getModelObject() {
		return notification;
	}

	private void fillUserInformation() {
		final String userEmail = notification.getAuthor().getEmail();

		final UserDataService userDataService = ClientServiceProvider.getInstance().getUserDataService();
		userIcon.setUrl(userDataService.getAvatarUrl(userEmail));
		userIcon.setTitle(userEmail);

		userName.setText(userEmail);

		userDataService.loadProfile(userEmail, new LoadProfileCallback() {

			@Override
			public void onProfileUnavailable(final Throwable cause) {}

			@Override
			public void onProfileLoaded(final PortableContactJsonObject profile) {
				userName.setText(profile.getDisplayName());
			}
		});
	}
}
