package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;

@Entity(name = "ScopeBindRelease")
@ConvertTo(ScopeBindReleaseAction.class)
public class ScopeBindReleaseActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String referenceId;

	@Column(name = "secundaryReferenceId")
	private String newReleaseDescription;

	@ConversionAlias("subAction")
	@OneToOne(cascade = CascadeType.ALL)
	private ModelActionEntity subAction;

	@ConversionAlias("releaseCreateAction")
	@OneToOne(cascade = CascadeType.ALL)
	private ModelActionEntity secundarySubAction;

	private int scopePriority;

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

	public void setScopePriority(final int scopePriority) {
		this.scopePriority = scopePriority;
	}

	public int getScopePriority() {
		return scopePriority;
	}

	public ModelActionEntity getSecundarySubAction() {
		return secundarySubAction;
	}

	public void setSecundarySubAction(final ModelActionEntity secundarySubAction) {
		this.secundarySubAction = secundarySubAction;
	}

}
