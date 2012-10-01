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
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@ConvertTo(NotificationEntity.class)
public class Notification implements Serializable {

	public enum NotificationType {
		TEXT,
		IMPEDIMENT;
	}

	private static final long serialVersionUID = 1L;

	@Element
	@ConversionAlias("id")
	private UUID id;

	@Attribute
	@ConversionAlias("message")
	private String message;

	@Attribute
	@ConversionAlias("timestamp")
	@IgnoredByDeepEquality
	private Date timestamp;

	@ElementList
	@ConversionAlias("recipients")
	private final List<User> recipients = new ArrayList<User>();

	@Attribute
	@ConversionAlias("type")
	@ConvertUsing(NotificationTypeConveter.class)
	private NotificationType type = NotificationType.TEXT;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected Notification() {}

	public UUID getId() {
		return id;
	}

	public List<User> getRecipients() {
		return recipients;
	}

	public String getMessage() {
		return message;
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

	protected void setMessage(final String message) {
		this.message = message;
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
}
