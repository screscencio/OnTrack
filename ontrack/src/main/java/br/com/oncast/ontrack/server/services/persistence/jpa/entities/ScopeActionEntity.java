package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ScopeActionEntity {

	@Id
	@GeneratedValue
	private long id;

	private Date timestamp;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}
}
