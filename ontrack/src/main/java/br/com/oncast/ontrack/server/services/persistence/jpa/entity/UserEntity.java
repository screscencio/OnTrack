package br.com.oncast.ontrack.server.services.persistence.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.user.User;

@ConvertTo(User.class)
@Entity
public class UserEntity {

	@Id
	@ConvertUsing(StringToUuidConverter.class)
	private String id;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "projectInvitationQuota", nullable = false)
	private int projectInvitationQuota;

	@Column(name = "projectCreationQuota", nullable = false)
	private int projectCreationQuota;

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

	public int getProjectInvitationQuota() {
		return projectInvitationQuota;
	}

	public void setProjectInvitationQuota(final int projectInvitationQuota) {
		this.projectInvitationQuota = projectInvitationQuota;
	}

	public int getProjectCreationQuota() {
		return projectCreationQuota;
	}

	public void setProjectCreationQuota(final int projectCreationQuota) {
		this.projectCreationQuota = projectCreationQuota;
	}

}
