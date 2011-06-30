package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ScopeRemoveActionEntity extends ActionEntity {

	@Column
	private String referenceId;

	@Column
	private int index;

	@Column
	private String description;

	@Column
	private String releaseId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(final String releaseId) {
		this.releaseId = releaseId;
	}

}
