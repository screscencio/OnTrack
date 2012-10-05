package br.com.oncast.ontrack.server.services.persistence.jpa.entity.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.NotificationTypeConveter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.Notification.NotificationType;

@Entity
@ConvertTo(Notification.class)
public class NotificationEntity {

	@Id
	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("id")
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	@ConversionAlias("timestamp")
	@Column(unique = false, nullable = false)
	private Date timestamp;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "recipient_id")
	@ConversionAlias("recipients")
	private List<NotificationRecipientEntity> recipients = new ArrayList<NotificationRecipientEntity>();

	@ConversionAlias("author")
	private String author;

	@ConversionAlias("project")
	@ConvertUsing(StringToUuidConverter.class)
	private String projectId;

	@ConversionAlias("referenceId")
	@ConvertUsing(StringToUuidConverter.class)
	@Column(unique = false, nullable = true)
	private String referenceId;

	@ConversionAlias("type")
	@ConvertUsing(NotificationTypeConveter.class)
	@Column(unique = false, nullable = false)
	private NotificationType type;

	@Column(name = "description", unique = false, nullable = true)
	@ConversionAlias("description")
	private String description;

	public NotificationEntity() {}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(final String projectId) {
		this.projectId = projectId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(final NotificationType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public List<NotificationRecipientEntity> getRecipients() {
		return recipients;
	}

	public void setRecipients(final List<NotificationRecipientEntity> recipients) {
		this.recipients = recipients;
	}

	public void addRecipient(final NotificationRecipientEntity notificationRecipientEntity) {
		this.recipients.add(notificationRecipientEntity);
	}
}
