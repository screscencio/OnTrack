package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class ModelActionSyncRequest implements DispatchRequest<VoidResult> {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	private List<ModelAction> actionList;

	private long projectId;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ModelActionSyncRequest() {}

	public ModelActionSyncRequest(final ProjectRepresentation projectRepresentation, final List<ModelAction> actionList) {
		this.projectId = projectRepresentation.getId();
		this.actionList = actionList;
	}

	public List<ModelAction> getActionList() {
		return actionList;
	}

	public long getProjectId() {
		return projectId;
	}
}
