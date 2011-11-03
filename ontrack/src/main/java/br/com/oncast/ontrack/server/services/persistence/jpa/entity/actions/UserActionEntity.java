package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.business.UserAction;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

@Entity
@ConvertTo(UserAction.class)
public class UserActionEntity {

	@Id
	@GeneratedValue
	@ConversionAlias("id")
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	@ConversionAlias("timestamp")
	private Date timestamp;

	@OneToOne(cascade = CascadeType.ALL)
	@ConversionAlias("action")
	private ModelActionEntity actionEntity;

	public UserActionEntity() {}

	public UserActionEntity(final ModelActionEntity actionEntity, final Date timestamp) {
		this.actionEntity = actionEntity;
		this.timestamp = timestamp;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public ModelActionEntity getActionEntity() {
		return actionEntity;
	}

	public void setActionEntity(final ModelActionEntity actionEntity) {
		this.actionEntity = actionEntity;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}
}
