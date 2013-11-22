package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity(name = "ScopeRemoveRollback")
@ConvertTo(ScopeRemoveRollbackAction.class)
public class ScopeRemoveRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_3)
	private String parentScopeId;

	@Column(name = ActionTableColumns.DESCRIPTION_TEXT, length = ActionTableColumns.DESCRIPTION_TEXT_LENGTH)
	private String description;

	@ConversionAlias("index")
	@Column(name = ActionTableColumns.INT_1)
	private int pos;

	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = "modelActionEntity_secundarySubActionList")
	@JoinTable(name = "ScopeRemoveRollbackAction_secundarySubActionList")
	private List<ModelActionEntity> childActionList;

	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = ActionTableColumns.ACTION_LIST)
	@JoinTable(name = "ScopeRemoveRollbackAction_subActionList")
	private List<ModelActionEntity> subActionList;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

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
