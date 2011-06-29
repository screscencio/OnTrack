package br.com.oncast.ontrack.shared.model.scope.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeRemoveAction implements ScopeAction {

	private UUID selectedScopeId;

	public ScopeRemoveAction(final UUID selectedScopeId) {
		this.selectedScopeId = selectedScopeId;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeRemoveAction() {}

	@Override
	public ScopeRemoveRollbackAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) { throw new UnableToCompleteActionException("Unable to remove root level."); }

		final List<ScopeRemoveRollbackAction> childActionList = new ArrayList<ScopeRemoveRollbackAction>();

		final Scope parent = selectedScope.getParent();
		final UUID parentScopeId = parent.getId();
		final String description = selectedScope.getDescription();

		for (final Scope child : new ArrayList<Scope>(selectedScope.getChildren()))
			childActionList.add(new ScopeRemoveAction(child.getId()).execute(context));

		final int index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);

		UUID releaseId;
		if (selectedScope.getRelease() != null) {
			releaseId = selectedScope.getRelease().getId();
			selectedScope.getRelease().removeScope(selectedScope);
			selectedScope.setRelease(null);
		}
		else releaseId = null;

		return new ScopeRemoveRollbackAction(parentScopeId, selectedScopeId, description, releaseId, index, childActionList);
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}
}