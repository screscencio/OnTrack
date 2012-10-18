package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;

@Entity(name = "TeamInvite")
@ConvertTo(TeamInviteAction.class)
public class TeamInviteActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String userId;

	@Column(name = ActionTableColumns.STRING_2)
	private String inviteeEmail;

	public TeamInviteActionEntity() {}

	public String getInviteeEmail() {
		return inviteeEmail;
	}

	public void setInviteeEmail(final String inviteeEmail) {
		this.inviteeEmail = inviteeEmail;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
