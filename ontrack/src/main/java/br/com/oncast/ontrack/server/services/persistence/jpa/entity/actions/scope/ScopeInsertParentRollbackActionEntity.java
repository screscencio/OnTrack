package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentRollbackAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity(name = "ScopeInsertParentRollback")
@ConvertTo(ScopeInsertParentRollbackAction.class)
public class ScopeInsertParentRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_3)
	private String newScopeId;

	@ConversionAlias("scopeUpdateAction")
	@OneToOne(cascade = CascadeType.ALL)
	private ModelActionEntity subAction;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getNewScopeId() {
		return newScopeId;
	}

	public void setNewScopeId(final String newScopeId) {
		this.newScopeId = newScopeId;
	}

	public ModelActionEntity getSubAction() {
		return subAction;
	}

	public void setSubAction(final ModelActionEntity subAction) {
		this.subAction = subAction;
	}

}
