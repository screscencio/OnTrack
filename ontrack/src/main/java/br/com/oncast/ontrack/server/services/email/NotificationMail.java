package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.shared.services.notification.Notification;

public class NotificationMail implements OnTrackMail {

	public NotificationMail(final Notification notification) {}

	@Override
	public String getSubject() {
		return "Notifição do OnTrack";
	}

	@Override
	public String getTemplatePath() {
		return "/br/com/oncast/ontrack/server/services/email/notificationMail.html";
	}

	@Override
	public MailVariableValuesMap getParameters() {
		final MailVariableValuesMap param = new MailVariableValuesMap();
		return param;
	}

	@Override
	public String getSendTo() {
		return null;
	}

	public void send() {
		// FIXME Auto-generated catch block

	}

}
