package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ScopeInsertChildActionEntity extends ScopeActionEntity implements ModelActionEntity {

	@Column
	private String referenceId;

	@Column
	private String newScopeId;

	@Column
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
