package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ActionExecutionContext implements HasUUID, Serializable {

	private static final long serialVersionUID = 1L;

	private final ModelAction reverseAction;

	private final Set<UUID> inferenceInfluencedScopeSet;

	private final UserAction action;

	public ActionExecutionContext(final UserAction action, final ModelAction reverseAction, final Set<UUID> inferenceInfluencedScopeSet) {
		this.action = action;
		this.reverseAction = reverseAction;
		this.inferenceInfluencedScopeSet = inferenceInfluencedScopeSet;
	}

	public ActionExecutionContext(final UserAction action, final ModelAction reverseAction) {
		this(action, reverseAction, new HashSet<UUID>());
	}

	public UserAction getUserAction() {
		return action;
	}

	public ModelAction getModelAction() {
		return getUserAction().getModelAction();
	}

	public UserAction getReverseUserAction() {
		final UserAction action = getUserAction();
		return new UserAction(reverseAction, action.getUserId(), action.getProjectId(), action.getExecutionTimestamp());
	}

	public Set<UUID> getInferenceInfluencedScopeSet() {
		return inferenceInfluencedScopeSet;
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public UUID getId() {
		return getUserAction().getUniqueId();
	}

	public UUID getUserId() {
		return getUserAction().getUserId();
	}

	public Date getExecutionTimestamp() {
		return getUserAction().getExecutionTimestamp();
	}

	public ActionContext createActionContext() {
		return new ActionContext(getUserId(), getExecutionTimestamp());
	}

	public ModelAction getReverseModelAction() {
		return reverseAction;
	}
}
