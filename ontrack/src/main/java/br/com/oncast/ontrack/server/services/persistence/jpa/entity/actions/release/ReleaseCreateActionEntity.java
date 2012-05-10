package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseCreateAction;

@Entity(name = "ReleaseCreate")
@ConvertTo(ReleaseCreateAction.class)
public class ReleaseCreateActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("referenceId")
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@ConversionAlias("description")
	@Column(name = ActionTableColumns.STRING_2, length = ActionTableColumns.STRING_2_LENGTH)
	private String description;

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("newReleaseId")
	@Column(name = ActionTableColumns.STRING_3)
	private String newReleaseId;

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
