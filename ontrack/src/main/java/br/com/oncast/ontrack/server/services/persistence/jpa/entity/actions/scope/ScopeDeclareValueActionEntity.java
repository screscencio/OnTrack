package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareValueAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ScopeDeclareValue")
@ConvertTo(ScopeDeclareValueAction.class)
public class ScopeDeclareValueActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean hasDeclaredValue;

	@Column(name = ActionTableColumns.INT_1)
	private float newDeclaredValue;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public boolean isHasDeclaredValue() {
		return hasDeclaredValue;
	}

	public void setHasDeclaredValue(final boolean hasDeclaredValue) {
		this.hasDeclaredValue = hasDeclaredValue;
	}

	public float getNewDeclaredValue() {
		return newDeclaredValue;
	}

	public void setNewDeclaredValue(final float newDeclaredValue) {
		this.newDeclaredValue = newDeclaredValue;
	}
}
