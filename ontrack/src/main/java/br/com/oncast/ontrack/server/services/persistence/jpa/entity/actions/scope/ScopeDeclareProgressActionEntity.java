package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;

@Entity(name = "ScopeDeclareProgress")
@ConvertTo(ScopeDeclareProgressAction.class)
public class ScopeDeclareProgressActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@Column(name = ActionTableColumns.DESCRIPTION_TEXT, length = ActionTableColumns.DESCRIPTION_TEXT_LENGTH)
	private String newProgressDescription;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ActionTableColumns.DATE_1)
	private Date timestamp;

	@ConversionAlias("subAction")
	@OneToOne(cascade = CascadeType.ALL)
	private ModelActionEntity subAction;

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

	public ModelActionEntity getSubAction() {
		return subAction;
	}

	public void setSubAction(final ModelActionEntity subAction) {
		this.subAction = subAction;
	}
}
