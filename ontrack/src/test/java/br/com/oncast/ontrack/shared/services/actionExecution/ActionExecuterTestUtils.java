package br.com.oncast.ontrack.shared.services.actionExecution;

import java.util.Set;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActionExecuterTestUtils extends ScopeActionExecuter {

	private static final class ModelActionMockImpl implements ScopeAction {
		private static final long serialVersionUID = 1L;

		@Override
		public UUID getReferenceId() {
			return null;
		}

		@Override
		public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
			return null;
		}

		@Override
		public boolean changesEffortInference() {
			return true;
		}

		@Override
		public boolean changesProgressInference() {
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
