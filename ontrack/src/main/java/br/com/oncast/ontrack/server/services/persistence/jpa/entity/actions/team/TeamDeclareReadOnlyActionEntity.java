package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareReadOnlyAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@ConvertTo(TeamDeclareReadOnlyAction.class)
@Entity(name = "TeamDeclareReadOnly")
public class TeamDeclareReadOnlyActionEntity extends ModelActionEntity {

	@Column(name = ActionTableColumns.STRING_1)
	@ConvertUsing(StringToUuidConverter.class)
	private String userId;

	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean readOnly;

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
