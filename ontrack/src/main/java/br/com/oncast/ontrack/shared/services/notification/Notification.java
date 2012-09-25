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
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(NotificationEntity.class)
public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	@Element
	@ConversionAlias("id")
	private UUID id;

	@Attribute
	@ConversionAlias("message")
	private String message;

	@Attribute
	@ConversionAlias("timestamp")
	private Date timestamp;

	@ElementList
	@ConversionAlias("recipients")
	private List<User> recipients = new ArrayList<User>();

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected Notification() {}

	protected Notification(final String message, final List<User> recipients) {
		this(new UUID(), message, new Date(), recipients);
	}

	protected Notification(final UUID id, final String message, final Date timestamp, final List<User> recipients) {
		this.id = id;
		this.message = message;
		this.timestamp = timestamp;
		this.recipients = recipients;
	}

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
}
