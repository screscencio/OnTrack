package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeBindReleaseAction;

@Entity
@ConvertTo(ScopeBindReleaseAction.class)
public class ScopeBindReleaseActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	private String newReleaseDescription;

	@OneToOne(cascade = CascadeType.ALL)
	private ModelActionEntity subAction;

	@OneToOne(cascade = CascadeType.ALL)
	private ModelActionEntity releaseCreateAction;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getNewReleaseDescription() {
		return newReleaseDescription;
	}

	public void setNewReleaseDescription(final String newReleaseDescription) {
		this.newReleaseDescription = newReleaseDescription;
	}

	public ModelActionEntity getSubAction() {
		return subAction;
	}

	public void setSubAction(final ModelActionEntity subAction) {
		this.subAction = subAction;
	}

	public ModelActionEntity getReleaseCreateAction() {
		return releaseCreateAction;
	}

	public void setReleaseCreateAction(final ModelActionEntity releaseCreateAction) {
		this.releaseCreateAction = releaseCreateAction;
	}

}
