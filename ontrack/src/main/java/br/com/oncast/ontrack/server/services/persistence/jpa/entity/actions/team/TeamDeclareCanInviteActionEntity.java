package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareCanInviteAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@ConvertTo(TeamDeclareCanInviteAction.class)
@Entity(name = "TeamDeclareCanInvite")
public class TeamDeclareCanInviteActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String userId;

	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean canInvite;

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public boolean isCanInvite() {
		return canInvite;
	}

	public void setCanInvite(final boolean canInvite) {
		this.canInvite = canInvite;
	}

}
