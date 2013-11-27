package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

public class ModelActionSyncRequest implements DispatchRequest<ModelActionSyncRequestResponse> {

	private List<UserAction> actionList;

	private boolean shouldReturnToSender = false;

	private UUID projectId;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ModelActionSyncRequest() {}

	public ModelActionSyncRequest(final UUID projectId, final List<UserAction> actionList) {
		this.projectId = projectId;
		this.actionList = actionList;
	}

	public ModelActionSyncRequest(final UserAction action) {
		this.projectId = action.getProjectId();
		this.actionList = new ArrayList<UserAction>();
		this.actionList.add(action);
	}

	public ModelActionSyncRequest setShouldReturnToSender(final boolean shouldReturnToSender) {
		this.shouldReturnToSender = shouldReturnToSender;
		return this;
	}

	public List<UserAction> getActionList() {
		return actionList;
	}

	public boolean shouldReturnToSender() {
		return shouldReturnToSender;
	}

	public UUID getProjectId() {
		return projectId;
	}
}
