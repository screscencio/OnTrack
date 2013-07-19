package br.com.oncast.ontrack.server.services.api.bean;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectContextApiRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID projectId;

	ProjectContextApiRequest() {}

	public ProjectContextApiRequest(final UUID projectId) {
		this.projectId = projectId;
	}

	public UUID getProjectId() {
		return projectId;
	}

}
