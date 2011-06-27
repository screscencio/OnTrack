package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ScopeMoveUpActionEntity extends ScopeActionEntity implements ModelActionEntity {

	@Column
	private String referenceId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}
}
