package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.annotations.MapTo;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveDownAction;

@Entity
@MapTo(ScopeMoveDownAction.class)
public class ScopeMoveDownActionEntity extends ModelActionEntity {

	private String referenceId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}
}
