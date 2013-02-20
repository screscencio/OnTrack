package br.com.oncast.ontrack.client.services.timesheet;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.places.timesheet.TimesheetPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class TimesheetServiceImpl implements TimesheetService {

	private final ApplicationPlaceController applicationPlaceController;
	private final ContextProviderService contextProviderService;

	public TimesheetServiceImpl(final ApplicationPlaceController applicationPlaceController, final ContextProviderService contextProviderService) {
		this.applicationPlaceController = applicationPlaceController;
		this.contextProviderService = contextProviderService;
	}

	@Override
	public void showTimesheetFor(final UUID releaseId) {
		applicationPlaceController.goTo(new TimesheetPlace(getCurrentProjectId(), releaseId, applicationPlaceController.getCurrentPlace(), true));
	}

	private ProjectContext getCurrentContext() {
		return contextProviderService.getCurrent();
	}

	private UUID getCurrentProjectId() {
		return getCurrentContext().getProjectRepresentation().getId();
	}

}
