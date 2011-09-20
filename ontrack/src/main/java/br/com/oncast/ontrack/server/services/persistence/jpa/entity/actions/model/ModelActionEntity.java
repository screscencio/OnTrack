package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ModelActionEntity {

	@Id
	@GeneratedValue
	private long id;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}
}
