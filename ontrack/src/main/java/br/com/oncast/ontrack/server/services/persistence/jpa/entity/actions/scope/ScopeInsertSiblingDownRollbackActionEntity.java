package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingDownRollbackAction;

@Entity
@ConvertTo(ScopeInsertSiblingDownRollbackAction.class)
public class ScopeInsertSiblingDownRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	@ConversionAlias("scopeUpdateRollbackAction")
	@OneToOne(cascade = CascadeType.ALL)
	private ScopeUpdateActionEntity scopeUpdateRollbackAction;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public ScopeUpdateActionEntity getScopeUpdateRollbackAction() {
		return scopeUpdateRollbackAction;
	}

	public void setScopeUpdateRollbackAction(final ScopeUpdateActionEntity scopeUpdateRollbackAction) {
		this.scopeUpdateRollbackAction = scopeUpdateRollbackAction;
	}
}
