package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class MultipleProjectContextRequestResponse implements DispatchResponse {

	private Set<Project> projects;

	public MultipleProjectContextRequestResponse() {}

	public MultipleProjectContextRequestResponse(final Set<Project> projects) {
		this.projects = projects;
	}

	public Set<ProjectContext> getProjects() {
		final Set<ProjectContext> contexts = new HashSet<ProjectContext>();
		for (final Project project : projects) {
			contexts.add(new ProjectContext(project));
		}
		return contexts;
	}

}
