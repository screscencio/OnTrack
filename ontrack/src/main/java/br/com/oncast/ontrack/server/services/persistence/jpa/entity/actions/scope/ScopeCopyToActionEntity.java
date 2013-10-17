package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeCopyToAction;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity(name = "ScopeCopyTo")
@ConvertTo(ScopeCopyToAction.class)
public class ScopeCopyToActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String sourceScopeId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_2)
	private String targetedParentId;

	@Column(name = ActionTableColumns.INT_1)
	private int targetIndex;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_3)
	private String newScopeId;

	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = ActionTableColumns.ACTION_LIST)
	@JoinTable(name = "ScopeCopyToAction_subActionList")
	private List<ModelActionEntity> subActionList;

	protected ScopeCopyToActionEntity() {}

	public String getSourceScopeId() {
		return sourceScopeId;
	}

	public void setSourceScopeId(final String sourceScopeId) {
		this.sourceScopeId = sourceScopeId;
	}

	public String getTargetedParentId() {
		return targetedParentId;
	}

	public void setTargetedParentId(final String targetedParentId) {
		this.targetedParentId = targetedParentId;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(final int targetIndex) {
		this.targetIndex = targetIndex;
	}

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(final List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
	}

	public String getNewScopeId() {
		return newScopeId;
	}

	public void setNewScopeId(String newScopeId) {
		this.newScopeId = newScopeId;
	}

}
