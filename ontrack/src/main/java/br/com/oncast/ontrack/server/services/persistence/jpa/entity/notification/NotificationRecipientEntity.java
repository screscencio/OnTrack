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

	@ConversionAlias("user")
	private String user;

	@ConversionAlias("readState")
	private boolean readState;

	public NotificationRecipientEntity() {}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public boolean isReadState() {
		return readState;
	}

	public void setReadState(final boolean readState) {
		this.readState = readState;
	}
}
