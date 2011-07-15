package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.EFFORT_SYMBOL;
import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.PROGRESS_SYMBOL;
import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.RELEASE_SYMBOL;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeRepresentationBuilder {

	private final Scope scope;
	private boolean includeScopeDescription;
	private boolean includeReleaseReference;
	private boolean includeEffort;
	private boolean includeProgress;

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

	public ScopeRepresentationBuilder excludeReleaseReference() {
		includeReleaseReference = false;
		return this;
	}

	public ScopeRepresentationBuilder includeEffort() {
		includeEffort = true;
		return this;
	}

	public ScopeRepresentationBuilder includeProgress() {
		includeProgress = true;
		return this;
	}

	public ScopeRepresentationBuilder includeEverything() {
		includeScopeDescription();
		includeReleaseReference();
		includeEffort();
		includeProgress();
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		if (includeScopeDescription) str.append(scope.getDescription());
		if (includeReleaseReference && scope.getRelease() != null) str.append(" " + RELEASE_SYMBOL + scope.getRelease().getFullDescription());
		if (includeEffort && scope.getEffort().hasDeclared()) str.append(" " + EFFORT_SYMBOL + scope.getEffort().getDeclared());
		if (includeProgress && scope.getProgress().hasDeclared()) str.append(" " + PROGRESS_SYMBOL + scope.getProgress().getDescription());

		return str.toString();
	}

}
