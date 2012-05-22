package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEndDayAction;

@ConvertTo(ReleaseDeclareEndDayAction.class)
@Entity(name = "ReleaseDeclareEndDay")
public class ReleaseDeclareEndDayActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ActionTableColumns.DATE_1)
	private Date endDay;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public Date getEndDay() {
		return endDay;
	}

	public void setEndDay(final Date endDay) {
		this.endDay = endDay;
	}

}
