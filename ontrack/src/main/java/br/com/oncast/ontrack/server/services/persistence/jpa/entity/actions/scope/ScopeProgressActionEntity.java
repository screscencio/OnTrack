package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.util.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeProgressAction;

@Entity
@ConvertTo(ScopeProgressAction.class)
public class ScopeProgressActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;
	private String newProgressDescription;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getNewProgressDescription() {
		return newProgressDescription;
	}

	public void setNewProgressDescription(final String newProgressDescription) {
		this.newProgressDescription = newProgressDescription;
	}

}
