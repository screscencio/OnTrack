package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ReleaseDeclareEstimatedVelocity")
@ConvertTo(ReleaseDeclareEstimatedVelocityAction.class)
public class ReleaseDeclareEstimatedVelocityActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String releaseId;

	@Column(name = ActionTableColumns.FLOAT_1, nullable = true)
	private Float estimatedVelocity;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(final String releaseId) {
		this.releaseId = releaseId;
	}

	public float getEstimatedVelocity() {
		return estimatedVelocity;
	}

	public void setEstimatedVelocity(final Float estimatedVelocity) {
		this.estimatedVelocity = estimatedVelocity;
	}

}
