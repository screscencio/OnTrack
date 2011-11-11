package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.simpleframework.xml.Attribute;

@Entity
public class ProjectRepresentation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Attribute
	@Id
	@GeneratedValue
	private long id;

	@Attribute
	@Column(name = "name", unique = false, nullable = false)
	private String name;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	public ProjectRepresentation() {}

	public ProjectRepresentation(final long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		final ProjectRepresentation other = (ProjectRepresentation) obj;

		if (id == 0) return false;
		if (id != other.id) return false;

		return true;
	}
}
