package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

@Entity
public class ScopeRemoveActionEntity extends ScopeActionEntity implements ModelActionEntity {

	@Column
	private String referenceId;

	@Column
	private UUID parentScopeId;

	@Column
	private int index;

	@Column
	private String description;

	@Column
	private UUID releaseId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public UUID getParentScopeId() {
		return parentScopeId;
	}

	public void setParentScopeId(final UUID parentScopeId) {
		this.parentScopeId = parentScopeId;
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

	public UUID getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(final UUID releaseId) {
		this.releaseId = releaseId;
	}

}
