package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.MapTo;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveRightAction;

@Entity
@MapTo(ScopeMoveRightAction.class)
public class ScopeMoveRightActionEntity extends ModelActionEntity {

	private String referenceId;
	private int position;
	private boolean wasIndexSet;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(final int position) {
		this.position = position;
	}

	public boolean isWasIndexSet() {
		return wasIndexSet;
	}

	public void setWasIndexSet(final boolean wasIndexSet) {
		this.wasIndexSet = wasIndexSet;
	}
}
