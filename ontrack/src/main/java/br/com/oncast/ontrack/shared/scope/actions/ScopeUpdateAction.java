package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.scope.pattern.ScopeParser;

public class ScopeUpdateAction implements ScopeAction {

	private final Scope selectedScope;

	private final String newDescription;
	private final String newRelease;

	private String oldDescription;
	private String oldRelease;

	public ScopeUpdateAction(final Scope scope, final String newPattern) {
		this.selectedScope = scope;

		final ScopeParser patternParser = new ScopeParser(newPattern);
		this.newDescription = patternParser.getScopeDescription();
		this.newRelease = patternParser.getReleaseDescription();
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		// public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		oldDescription = selectedScope.getDescription();
		// oldRelease = selectedScope.getRelease();

		selectedScope.setDescription(newDescription);
		// selectedScope.setRelease(newRelease);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		// public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		if (oldDescription == null) throw new UnableToCompleteActionException("The action cannot be rolled back because it has never been executed.");
		selectedScope.setDescription(oldDescription);
		// selectedScope.setRelease(oldRelease);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}
