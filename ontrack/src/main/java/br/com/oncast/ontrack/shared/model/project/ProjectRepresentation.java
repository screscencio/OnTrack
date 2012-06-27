package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.project.ProjectRepresentationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ProjectRepresentationEntity.class)
public class ProjectRepresentation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID id;

	@Attribute
	private String name;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectRepresentation() {}

	public ProjectRepresentation(final UUID id, final String name) {
		this.id = id;
		this.name = name;
	}

	public ProjectRepresentation(final String name) {
		this(new UUID(), name);
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ProjectRepresentation other = (ProjectRepresentation) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return id.toStringRepresentation() + " " + name;
	}
}
