package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;

@Entity(name = "ReleaseRename")
@ConvertTo(ReleaseRenameAction.class)
public class ReleaseRenameActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("referenceId")
	@Column(name = "referenceId")
	private String referenceId;

	@ConversionAlias("newReleaseDescription")
	@Column(name = "description")
	private String newReleaseDescription;

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
}
