package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorizationEntity;

@Root(name = "projectAuthorization")
public class ProjectAuthorizationXMLNode {

	@Attribute
	private long userId;

	@Attribute
	private long projectId;

	@SuppressWarnings("unused")
	// IMPORTANT The Simple Framework needs a default constructor for instantiate classes.
	private ProjectAuthorizationXMLNode() {}

	public ProjectAuthorizationXMLNode(final ProjectAuthorizationEntity authorization) {
		userId = authorization.getUser().getId();
		projectId = authorization.getProject().getId();
	}

	public long getUserId() {
		return userId;
	}

	public long getProjectId() {
		return projectId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (projectId ^ (projectId >>> 32));
		result = prime * result + (int) (userId ^ (userId >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ProjectAuthorizationXMLNode)) return false;
		final ProjectAuthorizationXMLNode other = (ProjectAuthorizationXMLNode) obj;
		if (projectId != other.projectId) return false;
		if (userId != other.userId) return false;
		return true;
	}

}
