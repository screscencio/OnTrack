package br.com.oncast.ontrack.shared.scope.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

public class ScopeRemoveAction implements ScopeAction {

	private UUID selectedScopeId;
	private List<ScopeRemoveAction> childList;
	private UUID parentScopeId;
	private int index;
	private String description;
	private Release release;

	public ScopeRemoveAction(final Scope selectedScope) {
		this.selectedScopeId = selectedScope.getId();
		this.childList = new ArrayList<ScopeRemoveAction>();
	}

	protected ScopeRemoveAction() {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		final Scope parent = selectedScope.getParent();
		if (parent == null) { throw new UnableToCompleteActionException("Unable to remove root level."); }

		this.parentScopeId = parent.getId();
		this.description = selectedScope.getDescription();

		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");

		for (final Scope child : selectedScope.getChildren())
			childList.add(new ScopeRemoveAction(child));

		for (final ScopeRemoveAction childRemoved : childList)
			childRemoved.execute(context);

		index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);
		release = selectedScope.getRelease();

		if (release != null) release.removeScope(selectedScope);
		selectedScope.setRelease(null);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope parent = context.findScope(parentScopeId);
		final Scope newScope = new Scope(description, selectedScopeId);

		parent.add(index, newScope);
		newScope.setRelease(release);
		if (release != null) release.addScope(newScope);

		for (final ScopeRemoveAction childId : childList) {
			childId.rollback(context);
		}
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}
}