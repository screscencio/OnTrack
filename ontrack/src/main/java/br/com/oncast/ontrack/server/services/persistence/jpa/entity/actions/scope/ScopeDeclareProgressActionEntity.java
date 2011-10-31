package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction;

@Entity(name = "ScopeDeclareProgress")
@ConvertTo(ScopeDeclareProgressAction.class)
public class ScopeDeclareProgressActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String referenceId;

	@Column(name = "description")
	private String newProgressDescription;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp")
	private Date timestamp;

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

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
