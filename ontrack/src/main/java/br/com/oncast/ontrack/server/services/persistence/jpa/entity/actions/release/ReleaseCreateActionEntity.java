package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ReleaseCreateActionDefault;

@Entity
@ConvertTo(ReleaseCreateActionDefault.class)
public class ReleaseCreateActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	private String referenceId;

	@ConvertUsing(StringToUuidConverter.class)
	private String newReleaseId;

	private String releaseDescription;

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

	public String getReleaseDescription() {
		return releaseDescription;
	}

	public void setReleaseDescription(final String releaseDescription) {
		this.releaseDescription = releaseDescription;
	}
}
