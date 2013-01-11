package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeAddTagAssociationAction;

@Entity(name = "ScopeAddTagAssociation")
@ConvertTo(ScopeAddTagAssociationAction.class)
public class ScopeAddTagAssociationActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String scopeId;

	@Column(name = ActionTableColumns.STRING_2)
	@ConvertUsing(StringToUuidConverter.class)
	private String tagId;

	@Column(name = ActionTableColumns.STRING_3)
	@ConvertUsing(StringToUuidConverter.class)
	private String metadataId;

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

	public String getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(String metadataId) {
		this.metadataId = metadataId;
	}

}
