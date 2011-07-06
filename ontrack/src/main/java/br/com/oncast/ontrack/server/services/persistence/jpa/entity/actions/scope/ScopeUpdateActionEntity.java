package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import java.util.List;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.util.converter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;

@Entity
@ConvertTo(ScopeUpdateAction.class)
public class ScopeUpdateActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	private String newDescription;

	private List<ModelAction> subActionList;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(final String newDescription) {
		this.newDescription = newDescription;
	}

	public List<ModelAction> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(final List<ModelAction> subActionList) {
		this.subActionList = subActionList;
	}
}
