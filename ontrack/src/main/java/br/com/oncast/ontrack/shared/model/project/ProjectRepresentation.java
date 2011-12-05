package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.simpleframework.xml.Attribute;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@Entity
// TODO ++Should we create another annotation that represents that a class must not be converted?
@ConvertTo(ProjectRepresentation.class)
public class ProjectRepresentation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@IgnoredByDeepEquality
	private long id;

	@Attribute
	@Column(name = "name", unique = false, nullable = false)
	private String name;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectRepresentation() {}

	public ProjectRepresentation(final long id, final String name) {
		this.id = id;
		this.name = name;
	}

	public ProjectRepresentation(final String name) {
		this.name = name;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return (id == 0) ? super.hashCode() : (int) id;
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

	@Override
	public String toString() {
		return id + " " + name;
	}
}
