package br.com.oncast.ontrack.shared.services.actionExecution;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActionExecutionContext {

	private final ModelAction reverseAction;
	private final Set<UUID> inferenceInfluencedScopeSet;

	public ActionExecutionContext(final ModelAction reverseAction, final Set<UUID> inferenceInfluencedScopeSet) {
		this.reverseAction = reverseAction;
		this.inferenceInfluencedScopeSet = inferenceInfluencedScopeSet;
	}

	public ActionExecutionContext(final ModelAction reverseAction) {
		this(reverseAction, new HashSet<UUID>());
	}

	public ModelAction getReverseAction() {
		return reverseAction;
	}

	public Set<UUID> getInferenceInfluencedScopeSet() {
		return inferenceInfluencedScopeSet;
	}
}
