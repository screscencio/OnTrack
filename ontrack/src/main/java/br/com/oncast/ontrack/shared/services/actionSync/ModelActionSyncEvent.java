package br.com.oncast.ontrack.shared.services.actionSync;

import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;
import br.com.oncast.ontrack.shared.utils.PrettyPrinter;

import java.util.ArrayList;
import java.util.List;

public class ModelActionSyncEvent implements ServerPushEvent {

	private static final long serialVersionUID = 1L;

	private List<UserAction> actionList;

	private UUID projectId;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ModelActionSyncEvent() {}

	public ModelActionSyncEvent(final UUID projectId, final List<UserAction> actionList) {
		this.projectId = projectId;
		this.actionList = actionList;
	}

	public ModelActionSyncEvent(final UserAction action) {
		this.projectId = action.getProjectId();
		this.actionList = new ArrayList<UserAction>();
		this.actionList.add(action);
	}

	public List<UserAction> getActionList() {
		return actionList;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public long getLastActionId() {
		long lastActionId = 0;
		for (final UserAction action : actionList) {
			lastActionId = Math.max(lastActionId, action.getSequencialId());
		}
		return lastActionId;
	}

	@Override
	public String toString() {
		return "[Project: " + projectId + "; " + PrettyPrinter.getSimpleNamesListString(actionList) + "]";
	}
}
