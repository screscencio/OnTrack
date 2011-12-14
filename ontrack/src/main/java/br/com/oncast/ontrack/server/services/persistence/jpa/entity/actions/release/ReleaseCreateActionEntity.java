package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseCreateActionDefault;

@Entity(name = "ReleaseCreate")
@ConvertTo(ReleaseCreateActionDefault.class)
public class ReleaseCreateActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("referenceId")
	@Column(name = "referenceId")
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("newReleaseId")
	@Column(name = "secundaryReferenceId")
	private String newReleaseId;

	@ConversionAlias("description")
	@Column(name = "description")
	private String description;

	@OneToOne(cascade = CascadeType.ALL)
	@ConversionAlias("subAction")
	private ModelActionEntity subAction;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getNewReleaseId() {
		return newReleaseId;
	}

	public void setNewReleaseId(final String newReleaseId) {
		this.newReleaseId = newReleaseId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ModelActionEntity getSubAction() {
		return subAction;
	}

	public void setSubAction(final ModelActionEntity subAction) {
		this.subAction = subAction;
	}
}
