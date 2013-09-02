package br.com.oncast.ontrack.server.services.persistence.jpa.entity.project;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "ProjectRepresentation")
@ConvertTo(ProjectRepresentation.class)
public class ProjectRepresentationEntity {

	@Id
	@ConvertUsing(StringToUuidConverter.class)
	private String id;

	@Column(name = "name", unique = false, nullable = false)
	private String name;

	@Column(name = "humanIdCounter", unique = false, nullable = false)
	private Long humanIdCounter;

	@Column(name = "removed", unique = false, nullable = true, updatable = true)
	private boolean removed;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Long getHumanIdCounter() {
		return humanIdCounter;
	}

	public void setHumanIdCounter(final Long humanIdCounter) {
		this.humanIdCounter = humanIdCounter;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(final boolean removed) {
		this.removed = removed;
	}
}
