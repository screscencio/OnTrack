package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.util.converter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveRollbackAction;

@Entity
@ConvertTo(ScopeRemoveRollbackAction.class)
public class ScopeRemoveRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	private String parentScopeId;

	private int index;

	private String description;

	@ConvertUsing(StringToUuidConverter.class)
	private String releaseId;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ScopeRemoveRollbackActionEntity> childActionList;

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

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(final String releaseId) {
		this.releaseId = releaseId;
	}

	public List<ScopeRemoveRollbackActionEntity> getChildActionList() {
		return childActionList;
	}

	public void setChildActionList(final List<ScopeRemoveRollbackActionEntity> childActionList) {
		this.childActionList = childActionList;
	}
}
