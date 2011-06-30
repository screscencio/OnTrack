package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ActionContainerEntity {

	@Id
	@GeneratedValue
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@OneToOne
	private ActionEntity action;

	public ActionContainerEntity() {
		timestamp = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setAction(final ActionEntity action) {
		this.action = action;
	}

	public ActionEntity getAction() {
		return action;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}
}
