package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ReleaseUpdatePriorityAction;

@Entity
@ConvertTo(ReleaseUpdatePriorityAction.class)
public class ReleaseUpdatePriorityActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String referenceId;

	@Column(name = "pos")
	private int targetIndex;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(final int targetIndex) {
		this.targetIndex = targetIndex;
	}

}
