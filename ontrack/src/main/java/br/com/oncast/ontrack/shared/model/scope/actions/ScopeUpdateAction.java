package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeUpdateActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationParser;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeUpdateActionEntity.class)
public class ScopeUpdateAction implements ScopeAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newPattern")
	private String newPattern;

	// TODO Parse value and generate specific equivalent actions for that. Remember to only create actions for things that would change. DO NOT STORE PATTERN.
	public ScopeUpdateAction(final UUID selectedScopeId, final String newPattern) {
		this.referenceId = selectedScopeId;
		this.newPattern = newPattern;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeUpdateAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(newPattern);
		final String newDescription = parser.getScopeDescription();
		final String newReleaseDescription = parser.getReleaseDescription();
		final int newDeclaredEffort = parser.getDeclaredEffort();
		final boolean hasDeclaredEffort = parser.hasDeclaredEffort();

		final Scope selectedScope = context.findScope(referenceId);
		final String oldDescription = selectedScope.getDescription();
		final Release oldRelease = selectedScope.getRelease();
		final String oldReleaseDescription = context.getReleaseDescriptionFor(oldRelease);

		// FIXME
		final boolean hadDeclared = selectedScope.getEffort().hasDeclared();
		final int oldDeclaredEffort = selectedScope.getEffort().getDeclared();

		if (oldRelease != null) oldRelease.removeScope(selectedScope);

		selectedScope.setDescription(newDescription);
		final Release newRelease = context.loadRelease(newReleaseDescription);
		selectedScope.setRelease(newRelease);
		if (newRelease != null) newRelease.addScope(selectedScope);

		if (hasDeclaredEffort) selectedScope.getEffort().setDeclared(newDeclaredEffort);
		else selectedScope.getEffort().resetDeclared();

		return new ScopeUpdateRollbackAction(referenceId, newPattern, oldDescription, oldReleaseDescription);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}
}
