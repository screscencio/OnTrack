package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.Convert;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;

@Entity
@Convert(ScopeMoveLeftAction.class)
public class ScopeMoveLeftActionEntity extends ModelActionEntity {

	private String referenceId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}
}