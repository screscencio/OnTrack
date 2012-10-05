package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.user.PortableContactJsonObject;
import br.com.oncast.ontrack.client.services.user.UserDataService;
import br.com.oncast.ontrack.client.services.user.UserDataService.LoadProfileCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.link.LinkFactory;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NotificationWidget extends Composite implements ModelWidget<Notification> {

	private static final NotificationWidgetMessages messages = GWT.create(NotificationWidgetMessages.class);

	private static NotificationWidgetUiBinder uiBinder = GWT.create(NotificationWidgetUiBinder.class);

	interface NotificationWidgetUiBinder extends UiBinder<Widget, NotificationWidget> {}

	interface NotificationWidgetStyle extends CssResource {
		String impediment();
	}

	@UiField
	NotificationWidgetStyle style;

	@UiField
	protected InlineHTML projectName;

	@UiField
	protected InlineHTML type;

	@UiField
	protected Label userName;

	@UiField
	protected Label timestamp;

	@UiField
	HorizontalPanel container;

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

		final ProjectRepresentation project = ClientServiceProvider.getInstance().getProjectRepresentationProvider()
				.getProjectRepresentation(notification.getProjectId());

		type.setHTML(LinkFactory.getLinkForAnnotation(project.getId(), notification.getReferenceId(), notification.getType().toString()));
		projectName.setHTML(LinkFactory.getLinkForProject(project));
		timestamp.setText(HumanDateFormatter.getDifferenceDate(notification.getTimestamp()));

		container.setStyleName(style.impediment());
		return true;
	}

	@Override
	public Notification getModelObject() {
		return notification;
	}

	private void fillUserInformation() {
		final String userEmail = notification.getAuthorMail();

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
