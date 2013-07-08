package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "TeamInvite")
@ConvertTo(TeamInviteAction.class)
public class TeamInviteActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String userId;

	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean canInvite;

	@Column(name = ActionTableColumns.BOOLEAN_2)
	private boolean readOnly;

	public TeamInviteActionEntity() {}

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

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(final boolean readOnly) {
		this.readOnly = readOnly;
	}

}
