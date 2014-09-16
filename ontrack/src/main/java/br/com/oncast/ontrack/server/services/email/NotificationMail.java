package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationType;

import java.util.Arrays;
import java.util.List;

public class NotificationMail implements OnTrackMail {

	private final User recipient;
	private final Notification notification;
	private final ProjectRepresentation project;
	private final User author;

	private static final List<NotificationType> IMPEDIMENT_TYPES = Arrays.asList(NotificationType.IMPEDIMENT_CREATED, NotificationType.IMPEDIMENT_SOLVED);

	private NotificationMail(final Notification notification, final User author, final User recipient, final ProjectRepresentation project) {
		this.notification = notification;
		this.author = author;
		this.recipient = recipient;
		this.project = project;
	}

	public static NotificationMail getMail(final Notification notification, final User author, final User recipient, final ProjectRepresentation project) {
		if (recipient == null) throw new IllegalArgumentException("The recipient is required");
		return new NotificationMail(notification, author, recipient, project);
	}

	@Override
	public String getSubject() {
		return "[OnTrack] Notifição";
	}

	@Override
	public String getTemplatePath() {
		return "/br/com/oncast/ontrack/server/services/email/" + (IMPEDIMENT_TYPES.contains(notification.getType()) ? "impediment" : "invitation") + "NotificationMail.html";
	}

	@Override
	public MailVariableValuesMap getParameters() {
		final MailVariableValuesMap param = new MailVariableValuesMap();
		param.put("userName", recipient.getName());
		param.put("projectId", project.getId());
		param.put("projectName", project.getName());
		param.put("authorName", author.getName());
		param.put("annotationId", notification.getReferenceId());
		param.put("annotationDescription", notification.getDescription());
		param.put("subjectDescription", notification.getReferenceDescription());
		param.put("action", notification.getType().simpleMessage(notification));
		return param;
	}

	@Override
	public List<String> getRecipients() {
		return Arrays.asList(recipient.getEmail());
	}

}
