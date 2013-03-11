package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.project.ProjectRepresentationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

@ConvertTo(ProjectRepresentationEntity.class)
public class ProjectRepresentation implements Serializable, HasUUID, Comparable<ProjectRepresentation> {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID id;

	@Attribute
	private String name;

	@Attribute
	private Long humanIdCounter;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectRepresentation() {}

	public ProjectRepresentation(final UUID id, final String name) {
		this.id = id;
		this.name = name;
		this.humanIdCounter = 0L;
	}

	public ProjectRepresentation(final String name) {
		this(new UUID(), name);
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	@Override
	public String toString() {
		return name + " (" + id.toString() + ")";
	}

	@Override
	public int compareTo(final ProjectRepresentation o) {
		return getName().compareTo(o.getName());
	}

	public long incrementHumanIdCounter() {
		return ++humanIdCounter;
	}

	public void setName(final String description) {
		name = description;
	}

}
