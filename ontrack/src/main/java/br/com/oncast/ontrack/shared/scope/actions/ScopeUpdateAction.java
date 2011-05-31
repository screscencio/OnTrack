package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.scope.stringrepresentation.ScopeRepresentationParser;

public class ScopeUpdateAction implements ScopeAction {

	private final Scope selectedScope;

	private final String newDescription;
	private final String newReleaseDescription;

	private String oldDescription;
	private String oldReleaseDescription;

	public ScopeUpdateAction(final Scope scope, final String newPattern) {
		this.selectedScope = scope;

		final ScopeRepresentationParser parser = new ScopeRepresentationParser(newPattern);
		this.newDescription = parser.getScopeDescription();
		this.newReleaseDescription = parser.getReleaseDescription();
	}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		oldDescription = selectedScope.getDescription();
		final Release oldRelease = selectedScope.getRelease();
		oldReleaseDescription = context.getReleaseDescription(oldRelease);
		if (oldRelease != null) oldRelease.removeScope(selectedScope);

		selectedScope.setDescription(newDescription);
		final Release newRelease = context.loadRelease(newReleaseDescription);
		selectedScope.setRelease(newRelease);
		if (newRelease != null) newRelease.addScope(selectedScope);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		if (oldDescription == null) throw new UnableToCompleteActionException("The action cannot be rolled back because it has never been executed.");

		final Release newRelease = selectedScope.getRelease();
		if (newRelease != null) newRelease.removeScope(selectedScope);

		selectedScope.setDescription(oldDescription);
		final Release oldRelease = context.loadRelease(oldReleaseDescription);
		selectedScope.setRelease(oldRelease);
		if (oldRelease != null) oldRelease.addScope(selectedScope);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}
