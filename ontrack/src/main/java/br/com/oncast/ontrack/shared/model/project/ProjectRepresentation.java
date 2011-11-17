package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.simpleframework.xml.Attribute;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

@Entity
// TODO ++ Should we create another annotation that represents that a class must not be converted?
@ConvertTo(ProjectRepresentation.class)
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

	public ProjectRepresentation(final long id, final String name) {
		this.id = id;
		this.name = name;
	}

	public ProjectRepresentation(final String name) {
		this.name = name;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
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
