package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveRollbackAction;

@Entity
@ConvertTo(ScopeRemoveRollbackAction.class)
public class ScopeRemoveRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	private String parentScopeId;

	private String description;

	@ConversionAlias("index")
	private int pos;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ScopeRemoveRollbackActionEntity> childActionList;

	@OneToMany(cascade = CascadeType.ALL)
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

	public List<ScopeRemoveRollbackActionEntity> getChildActionList() {
		return childActionList;
	}

	public void setChildActionList(final List<ScopeRemoveRollbackActionEntity> childActionList) {
		this.childActionList = childActionList;
	}

	public void setSubActionList(final List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
	}

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

}
