package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ReleaseScopeUpdatePriority")
@ConvertTo(ReleaseScopeUpdatePriorityAction.class)
public class ReleaseScopeUpdatePriorityActionEntity extends ModelActionEntity {

	@ConversionAlias("releaseReferenceId")
	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String releaseReferenceId;

	@ConversionAlias("scopeReferenceId")
	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_3)
	private String scopeReferenceId;

	@ConversionAlias("priority")
	@Column(name = ActionTableColumns.INT_1)
	private int priority;

	public String getReleaseReferenceId() {
		return releaseReferenceId;
	}

	public void setReleaseReferenceId(final String releaseReferenceId) {
		this.releaseReferenceId = releaseReferenceId;
	}

	public String getScopeReferenceId() {
		return scopeReferenceId;
	}

	public void setScopeReferenceId(final String scopeReferenceId) {
		this.scopeReferenceId = scopeReferenceId;
	}

	public void setPriority(final int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
}
