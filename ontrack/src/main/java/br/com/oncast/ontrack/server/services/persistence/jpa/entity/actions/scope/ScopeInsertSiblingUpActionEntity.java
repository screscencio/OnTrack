package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingUpAction;

@Entity
@ConvertTo(ScopeInsertSiblingUpAction.class)
public class ScopeInsertSiblingUpActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	private String newScopeId;

	private String pattern;

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

	public String getPattern() {
		return pattern;
	}

	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}
}
