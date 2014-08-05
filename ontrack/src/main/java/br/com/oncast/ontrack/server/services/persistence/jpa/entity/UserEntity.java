package br.com.oncast.ontrack.server.services.persistence.jpa.entity;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.User;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@ConvertTo(User.class)
@Entity
public class UserEntity {

	@Id
	@ConvertUsing(StringToUuidConverter.class)
	private String id;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "name", unique = false, nullable = true)
	private String name;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "userPictureId", unique = true, nullable = true)
	private String userPictureId;

	@Column(name = "globalProfile", nullable = false)
	private Profile globalProfile;

	@Column(name = "creationTimestamp", nullable = false)
	private Date creationTimestamp;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getUserPictureId() {
		return userPictureId;
	}

	public void setUserPictureId(final String userPictureId) {
		this.userPictureId = userPictureId;
	}

	public Profile getGlobalProfile() {
		return globalProfile;
	}

	public void setGlobalProfile(final Profile globalProfile) {
		this.globalProfile = globalProfile;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(final Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

}
