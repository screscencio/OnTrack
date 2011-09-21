package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeRemoveRollbackAction;

@Entity(name = "ScopeRemoveRollback")
@ConvertTo(ScopeRemoveRollbackAction.class)
public class ScopeRemoveRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "secundaryReferenceId")
	private String parentScopeId;

	@Column(name = "description")
	private String description;

	@ConversionAlias("index")
	@Column(name = "pos")
	private int pos;

	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = "modelActionEntity_secundarySubActionList")
	@JoinTable(name = "modelActionEntity_secundarySubActionList")
	private List<ModelActionEntity> childActionList;

	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = "modelActionEntity_subActionList")
	@JoinTable(name = "ScopeRemoveRollbackAction_subActionList")
	private List<ModelActionEntity> subActionList;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getParentScopeId() {
		return parentScopeId;
	}

	public void setParentScopeId(final String parentScopeId) {
		this.parentScopeId = parentScopeId;
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

	public List<ModelActionEntity> getChildActionList() {
		return childActionList;
	}

	public void setChildActionList(final List<ModelActionEntity> childActionList) {
		this.childActionList = childActionList;
	}

	public void setSubActionList(final List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
	}

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

}
