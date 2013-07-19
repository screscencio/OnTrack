package br.com.oncast.ontrack.server.services.api.bean;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectContextApiResponse extends BaseApiResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private ProjectContext projectContext;

	private long projectRevision;

	ProjectContextApiResponse() {}

	public ProjectContextApiResponse(final ProjectContext projectContext, final long projectRevision) {
		this.projectContext = projectContext;
		this.projectRevision = projectRevision;

	}

	public ProjectContextApiResponse(final Exception exception) {
		super(exception);
	}

	public ProjectContext getProjectContext() {
		return projectContext;
	}

	public long getProjectRevision() {
		return projectRevision;
	}

}
