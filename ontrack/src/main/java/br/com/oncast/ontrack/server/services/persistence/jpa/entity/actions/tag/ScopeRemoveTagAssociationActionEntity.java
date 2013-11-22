package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveTagAssociationAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ScopeRemoveTagAssociation")
@ConvertTo(ScopeRemoveTagAssociationAction.class)
public class ScopeRemoveTagAssociationActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String scopeId;

	@Column(name = ActionTableColumns.STRING_2)
	@ConvertUsing(StringToUuidConverter.class)
	private String tagId;

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

	public String getTagId() {
		return tagId;
	}

	public void setTagId(final String tagId) {
		this.tagId = tagId;
	}

}
