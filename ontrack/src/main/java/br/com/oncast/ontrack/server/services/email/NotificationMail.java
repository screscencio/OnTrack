package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationType;
import br.com.oncast.ontrack.shared.utils.AnnotationDescriptionParser;
import br.com.oncast.ontrack.shared.utils.AnnotationDescriptionParser.ParseHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationMail implements OnTrackMail {

	private final User recipient;
	private final Notification notification;
	private final ProjectRepresentation project;
	private final User author;
	private final List<User> users;

	private static final Map<NotificationType, String> TEMPLATE_POSTFIX = new HashMap<NotificationType, String>();
	static {
		TEMPLATE_POSTFIX.put(NotificationType.TEAM_INVITED, "invitation");
		TEMPLATE_POSTFIX.put(NotificationType.TEAM_REMOVED, "invitation");
	}

	private NotificationMail(final Notification notification, final User author, final User recipient, final List<User> users, final ProjectRepresentation project) {
		this.notification = notification;
		this.author = author;
		this.recipient = recipient;
		this.users = users;
		this.project = project;
	}

	public static NotificationMail getMail(final Notification notification, final User author, final User recipient, final List<User> users, final ProjectRepresentation project) {
		if (recipient == null) throw new IllegalArgumentException("The recipient is required");
		return new NotificationMail(notification, author, recipient, users, project);
	}

	@Override
	public String getSubject() {
		return "[OnTrack] Notifição";
	}

	@Override
	public String getTemplatePath() {
		return "/br/com/oncast/ontrack/server/services/email/" + (TEMPLATE_POSTFIX.containsKey(notification.getType()) ? TEMPLATE_POSTFIX.get(notification.getType()) : "annotation")
				+ "NotificationMail.html";
	}

	@Override
	public MailVariableValuesMap getParameters() {
		final MailVariableValuesMap param = new MailVariableValuesMap();
		param.put("userName", recipient.getName());
		param.put("projectId", project.getId());
		param.put("projectName", project.getName());
		param.put("authorName", author.getName());
		param.put("annotationId", notification.getReferenceId());
		param.put("annotationDescription", AnnotationDescriptionParser.parse(notification.getDescription(), users, new ParseHandler<User>() {
			@Override
			public String getReplacement(final User model) {
				return model.getName();
			}
		}));
		param.put("subjectDescription", notification.getReferenceDescription());
		param.put("action", notification.getType().simpleMessage(notification));
		return param;
	}

	@Override
	public List<String> getRecipients() {
		return Arrays.asList(recipient.getEmail());
	}

}
