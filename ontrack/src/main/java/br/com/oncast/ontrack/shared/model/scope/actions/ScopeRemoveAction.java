package br.com.oncast.ontrack.shared.model.scope.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeRemoveAction implements ScopeAction {

	private UUID selectedScopeId;
	private List<ScopeRemoveAction> childList;
	private UUID parentScopeId;
	private int index;
	private String description;
	private UUID releaseId;

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

		executeChildActions(context, selectedScope);

		index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);

		manageReleaseAssociation(selectedScope);
	}

	private void executeChildActions(final ProjectContext context, final Scope selectedScope) throws UnableToCompleteActionException {
		for (final Scope child : selectedScope.getChildren())
			childList.add(new ScopeRemoveAction(child));

		for (final ScopeRemoveAction childAction : childList)
			childAction.execute(context);
	}

	private void manageReleaseAssociation(final Scope selectedScope) {
		if (selectedScope.getRelease() != null) {
			releaseId = selectedScope.getRelease().getId();
			selectedScope.getRelease().removeScope(selectedScope);
			selectedScope.setRelease(null);
		}
		else releaseId = null;
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope parent = context.findScope(parentScopeId);
		final Scope newScope = new Scope(description, selectedScopeId);
		final Release release = releaseId != null ? context.findRelease(releaseId) : null;

		parent.add(index, newScope);

		newScope.setRelease(release);
		if (release != null) release.addScope(newScope);

		for (final ScopeRemoveAction childAction : childList) {
			childAction.rollback(context);
		}

		childList.clear();
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}
}