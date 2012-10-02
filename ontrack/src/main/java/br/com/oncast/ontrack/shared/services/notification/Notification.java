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

	public enum NotificationType {
		IMPEDIMENT;
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
	private List<User> recipients = null;

	@Element
	@ConversionAlias("author")
	private User author;

	@Element
	@ConversionAlias("project")
	private ProjectRepresentation projectRepresentation;

	@Element
	@ConversionAlias("referenceId")
	private UUID referenceId;

	@Attribute
	@ConversionAlias("description")
	private String description;

	@Attribute
	@ConversionAlias("type")
	@ConvertUsing(NotificationTypeConveter.class)
	private NotificationType type = NotificationType.IMPEDIMENT;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected Notification() {
		recipients = new ArrayList<User>();
	}

	public UUID getId() {
		return id;
	}

	public List<User> getRecipients() {
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

	protected void addReceipient(final User user) {
		if (this.recipients.contains(user)) return;
		this.recipients.add(user);
	}

	protected void setType(final NotificationType type) {
		this.type = type;
	}

	public User getAuthor() {
		return author;
	}

	protected void setAuthor(final User author) {
		this.author = author;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}

	protected void setProjectRepresentation(final ProjectRepresentation projectRepresentation) {
		this.projectRepresentation = projectRepresentation;
	}

	public UUID getReferenceId() {
		return referenceId;
	}

	protected void setReferenceId(final UUID referenceId) {
		this.referenceId = referenceId;
	}

}
