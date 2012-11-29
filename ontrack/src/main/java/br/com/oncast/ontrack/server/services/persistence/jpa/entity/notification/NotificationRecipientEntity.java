package br.com.oncast.ontrack.server.services.persistence.jpa.entity.notification;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.services.notification.NotificationRecipient;

@Entity
@ConvertTo(NotificationRecipient.class)
public class NotificationRecipientEntity {

	@Id
	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("id")
	private String id;

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("userId")
	private String userId;

	@ConversionAlias("readState")
	private boolean readState;

	public NotificationRecipientEntity() {}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String user) {
		this.userId = user;
	}

	public boolean isReadState() {
		return readState;
	}

	public void setReadState(final boolean readState) {
		this.readState = readState;
	}
}
