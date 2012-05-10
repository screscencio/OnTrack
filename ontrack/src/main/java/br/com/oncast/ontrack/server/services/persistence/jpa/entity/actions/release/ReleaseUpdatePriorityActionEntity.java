package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseUpdatePriorityAction;

@Entity(name = "ReleaseUpdatePriority")
@ConvertTo(ReleaseUpdatePriorityAction.class)
public class ReleaseUpdatePriorityActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@Column(name = ActionTableColumns.INT_1)
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
