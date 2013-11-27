package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ScopeAddAssociatedUser")
@ConvertTo(ScopeAddAssociatedUserAction.class)
public class ScopeAddAssociatedUserActionEntity extends ModelActionEntity {

	@Column(name = ActionTableColumns.STRING_1)
	@ConvertUsing(StringToUuidConverter.class)
	private String scopeId;

	@Column(name = ActionTableColumns.STRING_2)
	@ConvertUsing(StringToUuidConverter.class)
	private String userId;

	@Column(name = ActionTableColumns.STRING_3)
	@ConvertUsing(StringToUuidConverter.class)
	private String metadataId;

	public String getScopeId() {
		return scopeId;
	}

	public void setScopeId(final String scopeId) {
		this.scopeId = scopeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(final String metadataId) {
		this.metadataId = metadataId;
	}
}
