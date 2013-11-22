package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseCreateAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity(name = "ReleaseCreate")
@ConvertTo(ReleaseCreateAction.class)
public class ReleaseCreateActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("referenceId")
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@ConversionAlias("description")
	@Column(name = ActionTableColumns.DESCRIPTION_TEXT, length = ActionTableColumns.DESCRIPTION_TEXT_LENGTH)
	private String description;

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("newReleaseId")
	@Column(name = ActionTableColumns.STRING_2)
	private String newReleaseId;

	@OneToOne(cascade = CascadeType.ALL)
	@ConversionAlias("subAction")
	private ModelActionEntity subAction;

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
