package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeBindHumanIdAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ScopeBindHumanId")
@ConvertTo(ScopeBindHumanIdAction.class)
public class ScopeBindHumanIdActionEntity extends ModelActionEntity {

	@Column(name = ActionTableColumns.STRING_1)
	@ConvertUsing(StringToUuidConverter.class)
	private String scopeId;

	@Column(name = ActionTableColumns.STRING_2)
	@ConvertUsing(StringToUuidConverter.class)
	private String metadataId;

	@Column(name = ActionTableColumns.STRING_3)
	private String humanId;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getScopeId() {
		return scopeId;
	}

	public void setScopeId(final String scopeId) {
		this.scopeId = scopeId;
	}

	public String getHumanId() {
		return humanId;
	}

	public void setHumanId(final String humanId) {
		this.humanId = humanId;
	}

	public String getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(final String metadataId) {
		this.metadataId = metadataId;
	}

}
