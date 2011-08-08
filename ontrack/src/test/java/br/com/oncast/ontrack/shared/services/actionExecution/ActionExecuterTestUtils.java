package br.com.oncast.ontrack.shared.services.actionExecution;

import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActionExecuterTestUtils extends ActionExecuter {

	private static final class ModelActionMockImpl implements ModelAction {
		@Override
		public UUID getReferenceId() {
			return null;
		}

		@Override
		public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
			return null;
		}

		@Override
		public boolean changesProcessInference() {
			return true;
		}

		@Override
		public boolean changesEffortInference() {
			return true;
		}
	}

	public static Set<UUID> executeInferenceEnginesForTestingPurposes(final Scope scope) {
		return ActionExecuterTestUtils.executeInferenceEngines(new ModelActionMockImpl(), scope);
	}

	public static Scope getEffortInferenceBaseScopeForTestingPurposes(final ProjectContext context, final ModelAction action) throws ScopeNotFoundException {
		return getEffortInferenceBaseScope(context, action);
	}
}
