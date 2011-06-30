package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ScopeUpdateActionEntity extends ActionEntity {

	@Column
	private String referenceId;

	@Column
	private String pattern;

	@Column
	private String oldPattern;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	public String getOldPattern() {
		return oldPattern;
	}

	public void setOldPattern(final String oldPattern) {
		this.oldPattern = oldPattern;
	}

}
