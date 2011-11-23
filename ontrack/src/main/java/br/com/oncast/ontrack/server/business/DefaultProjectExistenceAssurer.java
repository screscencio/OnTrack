package br.com.oncast.ontrack.server.business;

import java.util.List;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class DefaultProjectExistenceAssurer {

	private static final Logger LOGGER = Logger.getLogger(DefaultProjectExistenceAssurer.class);

	public static void verify() {
		final BusinessLogic businessLogic = ServerServiceProvider.getInstance().getBusinessLogic();

		try {
			final List<ProjectRepresentation> projectList = businessLogic.retrieveProjectList();

			if (projectList.isEmpty()) createDefaultProject();
		}
		catch (final UnableToRetrieveProjectListException e) {
			LOGGER.error("An exception was found while trying to retrieve the project list.", e);
			throw new RuntimeException(e);
		}
		catch (final UnableToCreateProjectRepresentation e) {
			LOGGER.error("An exception was found while trying to create the default project.", e);
			throw new RuntimeException(e);
		}
	}

	private static void createDefaultProject() throws UnableToCreateProjectRepresentation {
		ServerServiceProvider.getInstance().getBusinessLogic().createProject("Default Project");
	}

}
