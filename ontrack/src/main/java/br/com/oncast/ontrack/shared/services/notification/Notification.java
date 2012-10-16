package br.com.oncast.ontrack.shared.services.notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.notification.NotificationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.NotificationTypeConveter;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@ConvertTo(NotificationEntity.class)
public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	@Element(required = false)
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

	@Attribute
	@ConversionAlias("author")
	private String authorMail;

	@Element
	@ConversionAlias("project")
	private UUID projectId;

	@Element(required = false)
	@ConversionAlias("referenceId")
	private UUID referenceId;

	@Attribute(required = false)
	@ConversionAlias("description")
	private String description;

	@Attribute(required = false)
	@ConversionAlias("referenceDescription")
	private String referenceDescription;

	@Element
	@ConversionAlias("type")
	@ConvertUsing(NotificationTypeConveter.class)
	private NotificationType type;

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

	public NotificationRecipient getRecipient(final User user) {
		for (final NotificationRecipient notificationRecipient : getRecipients()) {
			if (notificationRecipient.getUserMail().equals(user.getEmail())) return notificationRecipient;
		}
		return null;
	}

}
