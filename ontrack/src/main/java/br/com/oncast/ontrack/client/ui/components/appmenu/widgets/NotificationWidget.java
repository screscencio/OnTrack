package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.notification.NotificationClientUtils;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget.UserUpdateListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class NotificationWidget extends Composite implements ModelWidget<Notification> {

	private static final NotificationWidgetMessages messages = GWT.create(NotificationWidgetMessages.class);

	private static NotificationWidgetUiBinder uiBinder = GWT.create(NotificationWidgetUiBinder.class);

	interface NotificationWidgetUiBinder extends UiBinder<Widget, NotificationWidget> {}

	interface NotificationWidgetStyle extends CssResource {
		String impediment();

		String impedimentSolved();

		String normal();

		String read();
	}

	@UiField
	NotificationWidgetStyle style;

	@UiField
	protected InlineHTML notificationMessage;

	@UiField
	protected Label timestamp;

	@UiField
	protected Label userNameLabel;

	@UiField
	HorizontalPanel container;

	@UiField
	SimplePanel typeIndicator;

	@UiField(provided = true)
	UserWidget userWidget;

	private String userName;

	private final Notification notification;

	public NotificationWidget(final Notification modelBean) {
		this.notification = modelBean;
		userWidget = new UserWidget(new UserRepresentation(notification.getAuthorId()), new UserUpdateListener() {
			@Override
			public void onUserUpdate(final User user) {
				userName = user.getName();
				if (userNameLabel != null) update();
			}
		}, false);
		initWidget(uiBinder.createAndBindUi(this));

		setVisible(false);
		update();
		setVisible(true);
	}

	@Override
	public boolean update() {
		userNameLabel.setText(userName);
		notificationMessage.setHTML(notification.getType().selectMessage(messages, notification));

		timestamp.setText(HumanDateFormatter.get().formatDateRelativeToNow(notification.getTimestamp()));
		timestamp.setTitle(HumanDateFormatter.formatShortAbsoluteDate(notification.getTimestamp()));

		setStyleByType();

		updateReadStateStyle();

		return true;
	}

	private void updateReadStateStyle() {
		container.removeStyleName(style.read());
		if (NotificationClientUtils.getRecipientForCurrentUser(notification).getReadState()) container.addStyleName(style.read());
	}

	public void updateReadState(final boolean readState) {
		NotificationClientUtils.getRecipientForCurrentUser(notification).setReadState(readState);
		updateReadStateStyle();
	}

	@Override
	public Notification getModelObject() {
		return notification;
	}

	private void setStyleByType() {
		switch (notification.getType()) {
			case IMPEDIMENT_CREATED:
				typeIndicator.setStyleName(style.impediment());
				break;
			case IMPEDIMENT_SOLVED:
				typeIndicator.setStyleName(style.impedimentSolved());
				break;
			default:
				typeIndicator.setStyleName(style.normal());
				break;
		}
	}

}
