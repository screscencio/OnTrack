package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareProfileAction;
import br.com.oncast.ontrack.shared.model.user.Profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity(name = "TeamDeclareProfile")
@ConvertTo(TeamDeclareProfileAction.class)
public class TeamDeclareProfileActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String userId;

	@Enumerated(EnumType.STRING)
	@Column(name = ActionTableColumns.STRING_2)
	private Profile newProfile;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	protected TeamDeclareProfileActionEntity() {}

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public Profile getNewProfile() {
		return newProfile;
	}

	public void setNewProfile(final Profile newProfile) {
		this.newProfile = newProfile;
	}

}
