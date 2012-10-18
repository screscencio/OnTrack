package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@Root(name = "projectAuthorization")
public class ProjectAuthorizationXMLNode {

	@Element
	private UUID userId;

	@Element
	private UUID projectId;

	// IMPORTANT The Simple Framework needs a default constructor for instantiate classes.
	@SuppressWarnings("unused")
	private ProjectAuthorizationXMLNode() {}

	public ProjectAuthorizationXMLNode(final ProjectAuthorization authorization) {
		userId = authorization.getUserId();
		projectId = authorization.getProjectId();
	}

	public UUID getUserId() {
		return userId;
	}

	public UUID getProjectId() {
		return projectId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ProjectAuthorizationXMLNode other = (ProjectAuthorizationXMLNode) obj;
		if (projectId == null) {
			if (other.projectId != null) return false;
		}
		else if (!projectId.equals(other.projectId)) return false;
		if (userId == null) {
			if (other.userId != null) return false;
		}
		else if (!userId.equals(other.userId)) return false;
		return true;
	}

}
