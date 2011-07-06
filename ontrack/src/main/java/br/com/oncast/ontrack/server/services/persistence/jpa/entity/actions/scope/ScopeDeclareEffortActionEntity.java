package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.util.converter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeDeclareEffortAction;

@Entity
@ConvertTo(ScopeDeclareEffortAction.class)
public class ScopeDeclareEffortActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	private boolean hasDeclaredEffort;

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
