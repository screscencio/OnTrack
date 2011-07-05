package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.RELEASE_SYMBOL;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeRepresentationBuilder {

	private final Scope scope;
	private boolean includeScopeDescription;
	private boolean includeReleaseReference;
	private boolean includeEffort;

	public ScopeRepresentationBuilder(final Scope scope) {
		this.scope = scope;
	}

	public ScopeRepresentationBuilder includeScopeDescription() {
		includeScopeDescription = true;
		return this;
	}

	public ScopeRepresentationBuilder includeReleaseReference() {
		includeReleaseReference = true;
		return this;
	}

	public ScopeRepresentationBuilder includeEffort() {
		includeEffort = true;
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		if (includeScopeDescription) str.append(scope.getDescription());
		if (includeReleaseReference && scope.getRelease() != null) str.append(" " + RELEASE_SYMBOL + scope.getRelease().getFullDescription());
		if (includeEffort && scope.getEffort().hasDeclared()) str.append(" " + StringRepresentationSymbols.EFFORT_SYMBOL + scope.getEffort().getDeclared());

		return str.toString();
	}

	public ScopeRepresentationBuilder includeEverything() {
		includeScopeDescription();
		includeReleaseReference();
		includeEffort();
		return this;
	}
}
