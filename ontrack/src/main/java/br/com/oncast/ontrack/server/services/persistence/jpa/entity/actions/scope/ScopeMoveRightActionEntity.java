package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.util.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveRightAction;

@Entity
@ConvertTo(ScopeMoveRightAction.class)
public class ScopeMoveRightActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	@ConversionAlias("position")
	private int pos;

	private boolean wasIndexSet;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public boolean isWasIndexSet() {
		return wasIndexSet;
	}

	public void setWasIndexSet(final boolean wasIndexSet) {
		this.wasIndexSet = wasIndexSet;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(final int pos) {
		this.pos = pos;
	}

}
