package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ProjectRepresentationProvider {

	public abstract ProjectRepresentation getCurrent();

	public abstract void registerProjectListChangeListener(final ProjectListChangeListener projectListChangeListener);

	public abstract void createNewProject(final String projectName, final ProjectCreationListener projectCreationListener);

	public abstract void unregisterProjectListChangeListener(ProjectListChangeListener projectListChangeListener);

	public abstract void authorizeUser(String mail, ProjectAuthorizationCallback callback);

	public abstract ProjectRepresentation getProjectRepresentation(UUID projectReference);

}