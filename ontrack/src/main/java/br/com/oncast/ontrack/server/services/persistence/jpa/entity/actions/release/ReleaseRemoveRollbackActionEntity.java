package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeBindReleaseActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveRollbackAction;

@Entity(name = "ReleaseRemoveRollback")
@ConvertTo(ReleaseRemoveRollbackAction.class)
public class ReleaseRemoveRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("referenceId")
	@Column(name = "referenceId")
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("parentReleaseId")
	@Column(name = "secundaryReferenceId")
	private String parentReleaseId;

	@ConversionAlias("description")
	@Column(name = "description", length = 400)
	private String description;

	@ConversionAlias("index")
	@Column(name = "pos")
	private int pos;

	@Column(name = "ReleaseRemoveRollbackActionEntity_childActionList")
	@JoinTable(name = "ReleaseRemoveRollbackActionEntity_ChildAction")
	@OneToMany(cascade = CascadeType.ALL)
	private List<ReleaseRemoveRollbackActionEntity> childActionList;

	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = "ReleaseRemoveRollbackActionEntity_subActionRollbackList")
	@JoinTable(name = "ReleaseRemoveRollbackActionEntity_SubActionRollbackList")
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
