package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.util.converter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;

@Entity
@ConvertTo(ScopeMoveLeftAction.class)
public class ScopeMoveLeftActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}
}
