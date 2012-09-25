package br.com.oncast.ontrack.server.services.persistence.jpa.entity.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

@Entity(name = "ProjectRepresentation")
@ConvertTo(ProjectRepresentation.class)
public class ProjectRepresentationEntity {

	@Id
	@ConvertUsing(StringToUuidConverter.class)
	private String id;

	@Column(name = "name", unique = false, nullable = false)
	private String name;

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
}
