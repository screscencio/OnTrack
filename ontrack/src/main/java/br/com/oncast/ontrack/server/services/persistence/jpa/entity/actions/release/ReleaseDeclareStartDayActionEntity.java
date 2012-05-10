package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareStartDayAction;

@Entity(name = "ReleaseDeclareStartDay")
@ConvertTo(ReleaseDeclareStartDayAction.class)
public class ReleaseDeclareStartDayActionEntity extends ModelActionEntity {

	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@Column(name = ActionTableColumns.DATE_1)
	private Date date;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

}
