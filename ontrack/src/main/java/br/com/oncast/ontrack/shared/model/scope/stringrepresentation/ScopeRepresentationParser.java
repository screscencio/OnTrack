package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Parse a description and translate it into a scope description and a release description.
 */
public class ScopeRepresentationParser {

	private String scopeDescription;
	private String releaseDescription;
	private int declaredEffort;

	private static final String TAGS = "!@#$%=";
	// TODO Include TAGS into the patterns below
	private static final String FULL_PATTERN = "(.*)\\s+([!@#$%=].+)*";
	private static final String RELEASE_PATTERN = "@([^!@#$%=]+)";
	private static final String EFFORT_PATTERN = "#([^!@#$%=]+)";

	private final RegExp SCOPE_REGEX = RegExp.compile(FULL_PATTERN, "gi");

	// FIXME Delete MalformedScopeRepresentation
	public ScopeRepresentationParser(final String scopeRepresentation) {
		final MatchResult matchResult = SCOPE_REGEX.exec(scopeRepresentation);
		setScopeDescription(matchResult.getGroup(1));

		String tagsRepresentation = matchResult.getGroup(2);
		if (tagsRepresentation == null) return;

		tagsRepresentation = removeUnusedSymbols(tagsRepresentation);
		extractReleaseDescription(tagsRepresentation);
		extractDeclaredEffort(tagsRepresentation);
	}

	private String removeUnusedSymbols(final String tagsRepresentation) {
		return tagsRepresentation.replaceAll("[\"']", "");
	}

	private void setScopeDescription(final String result) {
		if (result == null) scopeDescription = "";
		else scopeDescription = result.trim();
	}

	private void extractReleaseDescription(final String tagsRepresentation) {
		final MatchResult result = RegExp.compile(RELEASE_PATTERN, "gi").exec(tagsRepresentation);
		releaseDescription = result.getGroup(1);
	}

	private void extractDeclaredEffort(final String tagsRepresentation) {
		final MatchResult result = RegExp.compile(EFFORT_PATTERN, "gi").exec(tagsRepresentation);
		if (result.getGroup(1) != null) declaredEffort = Integer.parseInt(result.getGroup(1));
	}

	public String getScopeDescription() {
		return scopeDescription;
	}

	public String getReleaseDescription() {
		return releaseDescription;
	}

	public int getDeclaredEffort() {
		return declaredEffort;
	}
}
