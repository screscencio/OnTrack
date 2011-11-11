package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.List;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// FIXME Use ProjectRepresentation in the server to save/validate/... the action list.
public class ModelActionSyncRequest extends ProjectContextRequest {

	private static final long serialVersionUID = 1L;

	private UUID clientId;

	private List<ModelAction> actionList;

	private ProjectRepresentation projectRepresentation;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ModelActionSyncRequest() {}

	public ModelActionSyncRequest(final UUID clientId, final ProjectRepresentation projectRepresentation, final List<ModelAction> actionList) {
		this.clientId = clientId;
		this.projectRepresentation = projectRepresentation;
		this.actionList = actionList;
	}

	public UUID getClientId() {
		return clientId;
	}

	public List<ModelAction> getActionList() {
		return actionList;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}
}
