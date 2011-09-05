package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeBindReleaseActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ReleaseRemoveRollbackAction;

@Entity
@ConvertTo(ReleaseRemoveRollbackAction.class)
public class ReleaseRemoveRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	private String parentReleaseId;

	private String description;

	@ConversionAlias("index")
	private int pos;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ReleaseRemoveRollbackActionEntity> childActionList;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ScopeBindReleaseActionEntity> subActionRollbackList;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getParentReleaseId() {
		return parentReleaseId;
	}

	public void setParentReleaseId(final String parentReleaseId) {
		this.parentReleaseId = parentReleaseId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(final int pos) {
		this.pos = pos;
	}

	public List<ReleaseRemoveRollbackActionEntity> getChildActionList() {
		return childActionList;
	}

	public void setChildActionList(final List<ReleaseRemoveRollbackActionEntity> childActionList) {
		this.childActionList = childActionList;
	}

	public List<ScopeBindReleaseActionEntity> getSubActionRollbackList() {
		return subActionRollbackList;
	}

	public void setSubActionRollbackList(final List<ScopeBindReleaseActionEntity> subActionRollbackList) {
		this.subActionRollbackList = subActionRollbackList;
	}
}
