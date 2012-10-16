package br.com.oncast.ontrack.shared.services.notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import br.com.oncast.ontrack.client.i18n.NotificationMessageCode;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationWidgetMessages;
import br.com.oncast.ontrack.client.utils.link.LinkFactory;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.notification.NotificationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.NotificationTypeConveter;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@Root(name = "notification")
@ConvertTo(NotificationEntity.class)
public class Notification implements Serializable {

	public enum NotificationType implements NotificationMessageCode {
		IMPEDIMENT_CREATED() {
			@Override
			public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
				return messages.impedimentCreatedNotificationWidgetMessage(getAnnotationLinkFor(notification),
						notification.getReferenceDescription(),
						getProjectLinkFor(notification));
			}
		},
		IMPEDIMENT_SOLVED() {
			@Override
			public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
				return messages.impedimentSolvedNotificationWidgetMessage(getAnnotationLinkFor(notification),
						notification.getReferenceDescription(),
						getProjectLinkFor(notification));
			}
		},
		PROGRESS_DECLARED() {
			@Override
			public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
				final String descriptionLink = getScopeLinkFor(notification);
				final String projectLink = getProjectLinkFor(notification);

				if (notification.getDescription().equals("Done")) return messages.progressDoneNotificationWidgetMessage(descriptionLink, projectLink);

				if (notification.getDescription().isEmpty()) return messages.progressNotStartedNotificationWidgetMessage(descriptionLink, projectLink);

				return messages.progressUnderworkNotificationWidgetMessage(descriptionLink, projectLink);
			}
		},
		ANNOTATION_CREATED() {
			@Override
			public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
				return messages.annotationCreatedNotificationWidgetMessage(getAnnotationLinkFor(notification),
						notification.getReferenceDescription(),
						getProjectLinkFor(notification));
			}
		};

		@Override
		public abstract String selectMessage(final NotificationWidgetMessages messages, final Notification notification);

		private static String getProjectLinkFor(final Notification notification) {
			final ProjectRepresentation project = ClientServiceProvider.getInstance().getProjectRepresentationProvider()
					.getProjectRepresentation(notification.getProjectId());

			return LinkFactory.getLinkForProject(project).asString();
		}

		private static String getAnnotationLinkFor(final Notification notification) {
			return LinkFactory.getLinkForAnnotation(notification.getProjectId(), notification.getReferenceId(), notification.getDescription()).asString();
		}

		private static String getScopeLinkFor(final Notification notification) {
			return LinkFactory.getScopeLinkFor(notification.getProjectId(), notification.getReferenceId(), notification.getReferenceDescription()).asString();
		}
	}

	private static final long serialVersionUID = 1L;

	@Element
	@ConversionAlias("id")
	private UUID id;

	@Attribute
	@ConversionAlias("timestamp")
	@IgnoredByDeepEquality
	private Date timestamp;

	@ElementList
	@ConversionAlias("recipients")
	@IgnoredByDeepEquality
	private List<NotificationRecipient> recipients = null;

	@Element
	@ConversionAlias("author")
	private String authorMail;

	@Element
	@ConversionAlias("project")
	private UUID projectId;

	@Element
	@ConversionAlias("referenceId")
	private UUID referenceId;

	@Attribute
	@ConversionAlias("description")
	private String description;

	@Attribute
	@ConversionAlias("referenceDescription")
	private String referenceDescription;

	@Attribute
	@ConversionAlias("type")
	@ConvertUsing(NotificationTypeConveter.class)
	private NotificationType type = null;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected Notification() {
		recipients = new ArrayList<NotificationRecipient>();
	}

	public UUID getId() {
		return id;
	}

	public List<String> getRecipientsAsUserMails() {
		final List<String> users = new ArrayList<String>();
		for (final NotificationRecipient recipient : recipients) {
			users.add(recipient.getUserMail());
		}
		return users;
	}

	public UUID getProjectId() {
		return projectId;
	}

	protected void setProjectId(final UUID projectId) {
		this.projectId = projectId;
	}

	public List<NotificationRecipient> getRecipients() {
		return recipients;
	}

	public String getDescription() {
		return description;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public NotificationType getType() {
		return type;
	}

	protected void setId(final UUID id) {
		this.id = id;
	}

	protected void setDescription(final String message) {
		this.description = message;
	}

	protected void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	protected void addReceipient(final NotificationRecipient recipient) {
		if (this.recipients.contains(recipient)) return;
		this.recipients.add(recipient);
	}

	protected void setType(final NotificationType type) {
		this.type = type;
	}

	public String getAuthorMail() {
		return authorMail;
	}

	protected void setAuthor(final User author) {
		this.authorMail = author.getEmail();
	}

	public UUID getProjectReference() {
		return projectId;
	}

	protected void setProjectRepresentation(final ProjectRepresentation projectRepresentation) {
		this.projectId = projectRepresentation.getId();
	}

	public UUID getReferenceId() {
		return referenceId;
	}

	protected void setReferenceId(final UUID referenceId) {
		this.referenceId = referenceId;
	}

	public String getReferenceDescription() {
		return referenceDescription;
	}

	public void setReferenceDescription(final String referenceDescription) {
		this.referenceDescription = referenceDescription;
	}
}
