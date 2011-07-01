package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.Convert;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateRollbackAction;

@Entity
@Convert(ScopeUpdateRollbackAction.class)
public class ScopeUpdateRollbackActionEntity extends ModelActionEntity {

	private String referenceId;
	private String newPattern;
	private String oldDescription;
	private String oldReleaseDescription;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getNewPattern() {
		return newPattern;
	}

	public void setNewPattern(final String newPattern) {
		this.newPattern = newPattern;
	}

	public String getOldDescription() {
		return oldDescription;
	}

	public void setOldDescription(final String oldDescription) {
		this.oldDescription = oldDescription;
	}

	public String getOldReleaseDescription() {
		return oldReleaseDescription;
	}

	public void setOldReleaseDescription(final String oldReleaseDescription) {
		this.oldReleaseDescription = oldReleaseDescription;
	}
}
