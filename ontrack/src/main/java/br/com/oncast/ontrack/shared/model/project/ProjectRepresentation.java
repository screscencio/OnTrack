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
public class ProjectRepresentation implements Serializable, HasUUID {

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

}
