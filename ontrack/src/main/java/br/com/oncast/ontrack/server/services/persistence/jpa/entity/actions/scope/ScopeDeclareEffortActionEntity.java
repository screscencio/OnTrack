package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;

@Entity(name = "ScopeDeclareEffort")
@ConvertTo(ScopeDeclareEffortAction.class)
public class ScopeDeclareEffortActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean hasDeclaredEffort;

	@Column(name = ActionTableColumns.FLOAT_1)
	private float newDeclaredEffort;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public boolean isHasDeclaredEffort() {
		return hasDeclaredEffort;
	}

	public void setHasDeclaredEffort(final boolean hasDeclaredEffort) {
		this.hasDeclaredEffort = hasDeclaredEffort;
	}

	public float getNewDeclaredEffort() {
		return newDeclaredEffort;
	}

	public void setNewDeclaredEffort(final float newDeclaredEffort) {
		this.newDeclaredEffort = newDeclaredEffort;
	}
}
