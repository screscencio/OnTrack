package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareEffortAction;

@Entity(name = "ScopeDeclareEffort")
@ConvertTo(ScopeDeclareEffortAction.class)
public class ScopeDeclareEffortActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String referenceId;

	@Column(name = "boleano")
	private boolean hasDeclaredEffort;

	@Column(name = "pos")
	private int newDeclaredEffort;

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

	public int getNewDeclaredEffort() {
		return newDeclaredEffort;
	}

	public void setNewDeclaredEffort(final int newDeclaredEffort) {
		this.newDeclaredEffort = newDeclaredEffort;
	}
}
