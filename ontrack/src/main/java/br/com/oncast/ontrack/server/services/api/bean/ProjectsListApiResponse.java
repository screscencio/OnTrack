package br.com.oncast.ontrack.server.services.api.bean;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectsListApiResponse extends BaseApiResponse {

	private static final long serialVersionUID = 1L;

	@XmlElementWrapper(name = "projectList")
	private List<ProjectRepresentation> projectList;

	ProjectsListApiResponse() {}

	public ProjectsListApiResponse(final Exception exception) {
		super(exception);
	}

	public ProjectsListApiResponse(final List<ProjectRepresentation> projectList) {
		this.projectList = projectList;
	}

	public List<ProjectRepresentation> getProjectList() {
		return projectList;
	}

}
