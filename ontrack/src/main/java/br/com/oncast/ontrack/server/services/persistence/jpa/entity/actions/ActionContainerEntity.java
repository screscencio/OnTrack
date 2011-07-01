package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;

@Entity(name = "ActionContainer")
public class ActionContainerEntity {

	@Id
	@GeneratedValue
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@OneToOne
	private ModelActionEntity actionEntity;

	public ActionContainerEntity() {}

	public ActionContainerEntity(final ModelActionEntity actionEntity, final Date timestamp) {
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
