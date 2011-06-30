package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ScopeMoveLeftActionEntity extends ActionEntity {

	@Column
	private String referenceId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}
}
