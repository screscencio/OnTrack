package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingUpAction;

@Entity
@ConvertTo(ScopeInsertSiblingUpAction.class)
public class ScopeInsertSiblingUpActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	private String newScopeId;

	@ConversionAlias("scopeUpdateAction")
	@OneToOne(cascade = CascadeType.ALL)
	private ScopeUpdateActionEntity scopeUpdateAction;

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

	public ScopeUpdateActionEntity getScopeUpdateAction() {
		return scopeUpdateAction;
	}

	public void setScopeUpdateAction(final ScopeUpdateActionEntity scopeUpdateAction) {
		this.scopeUpdateAction = scopeUpdateAction;
	}
}
