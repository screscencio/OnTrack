package br.com.oncast.ontrack.server.services.persistence.jpa.entity.notification;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.IgnoreByConversion;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.notification.NotificationRecipient;

@Entity
@ConvertTo(NotificationRecipient.class)
public class NotificationRecipientEntity {

	@Id
	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("id")
	private String id;

	@ConversionAlias("user")
	@OneToOne
	@JoinColumn(name = "user", nullable = false, updatable = false)
	private User user;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@IgnoreByConversion
	private NotificationEntity notification;

	public NotificationRecipientEntity() {}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public NotificationEntity getNotification() {
		return notification;
	}

	public void setNotification(final NotificationEntity notification) {
		this.notification = notification;
	}

}
