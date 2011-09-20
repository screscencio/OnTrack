package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveUpAction;

@Entity
@ConvertTo(ScopeMoveUpAction.class)
public class ScopeMoveUpActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String referenceId;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}
}
