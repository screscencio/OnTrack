package br.com.oncast.ontrack.shared.scope.stringrepresentation;

import br.com.oncast.ontrack.shared.scope.Scope;

public class ScopeRepresentationBuilder {

	private final Scope scope;
	private boolean includeScopeDescription;
	private boolean includeReleaseReference;

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

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		if (includeScopeDescription) str.append(scope.getDescription());
		if (includeReleaseReference && scope.getRelease() != null) {
			final String releaseFullDescription = scope.getRelease().getFullDescription();
			str.append(releaseFullDescription.contains(" ") ? " @\"" + releaseFullDescription + "\"" : " @" + releaseFullDescription);
		}

		return str.toString();
	}
}
