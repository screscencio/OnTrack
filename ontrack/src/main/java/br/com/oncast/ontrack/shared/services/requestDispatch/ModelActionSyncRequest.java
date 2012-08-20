package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ModelActionSyncRequest implements DispatchRequest<VoidResult> {

	private List<ModelAction> actionList;

	private UUID projectId;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ModelActionSyncRequest() {}

	public ModelActionSyncRequest(final ProjectRepresentation projectRepresentation, final List<ModelAction> actionList) {
		this(projectRepresentation.getId(), actionList);
	}

	public ModelActionSyncRequest(final UUID projectId, final List<ModelAction> actionList) {
		this.projectId = projectId;
		this.actionList = actionList;
	}

	public List<ModelAction> getActionList() {
		return actionList;
	}

	public UUID getProjectId() {
		return projectId;
	}
}
