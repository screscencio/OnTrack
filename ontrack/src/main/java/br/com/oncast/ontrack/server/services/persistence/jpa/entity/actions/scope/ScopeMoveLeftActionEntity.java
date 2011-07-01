package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.annotations.MapTo;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;

@Entity
@MapTo(ScopeMoveLeftAction.class)
public class ScopeMoveLeftActionEntity extends ModelActionEntity {

	private String referenceId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}
}
