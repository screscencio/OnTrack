package br.com.oncast.ontrack.shared.services.notification;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.notification.NotificationRecipientEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(NotificationRecipientEntity.class)
public class NotificationRecipient implements Serializable {

	private static final long serialVersionUID = 1L;

	@Element
	@ConversionAlias("id")
	private UUID id;

	@Element
	@ConversionAlias("userId")
	private UUID userId;

	@Attribute
	@ConversionAlias("readState")
	private boolean readState;

	protected NotificationRecipient() {}

	public NotificationRecipient(final UUID userId) {
		id = new UUID();
		setUserId(userId);
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final NotificationRecipient other = (NotificationRecipient) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	public void setReadState(final boolean read) {
		this.readState = read;
	}

	public boolean getReadState() {
		return readState;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(final UUID userId) {
		this.userId = userId;
	}
}
