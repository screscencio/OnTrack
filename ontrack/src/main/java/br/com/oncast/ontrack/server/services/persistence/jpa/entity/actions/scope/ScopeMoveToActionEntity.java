package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveToAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@ConvertTo(ScopeMoveToAction.class)
@Entity(name = "ScopeMoveTo")
public class ScopeMoveToActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String movingScopeId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_2)
	private String desiredParentId;

	@Column(name = ActionTableColumns.INT_1)
	private int desiredIndex;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getMovingScopeId() {
		return movingScopeId;
	}

	public void setMovingScopeId(final String movingScopeId) {
		this.movingScopeId = movingScopeId;
	}

	public String getDesiredParentId() {
		return desiredParentId;
	}

	public void setDesiredParentId(final String desiredParentId) {
		this.desiredParentId = desiredParentId;
	}

	public int getDesiredIndex() {
		return desiredIndex;
	}

	public void setDesiredIndex(final int desiredIndex) {
		this.desiredIndex = desiredIndex;
	}

}
