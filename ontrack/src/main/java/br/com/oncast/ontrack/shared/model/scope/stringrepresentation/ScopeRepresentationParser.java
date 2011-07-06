package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.EFFORT_SYMBOL;
import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.RELEASE_SYMBOL;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Parse a description and translate it into a scope description and a release description.
 */
// TODO Try to pre-compile regex expressions.
public class ScopeRepresentationParser {

	private String scopeDescription;
	private String releaseDescription;
	private int declaredEffort;
	private boolean hasDeclaredEffort;

	private static final String TAGS = StringRepresentationSymbols.getConcatenedSymbols();

	// 6 - 18
	// private final RegExp DESCRIPTION_REGEX = RegExp.compile("^([^" + TAGS + "]*[\\s]+)?([" + TAGS + "].+)*$", "gi");

	// 6 - 18
	private final RegExp DESCRIPTION_REGEX = RegExp.compile("^(([^" + TAGS + "]*[\\s]+)?([" + TAGS + "].+)*|([^" + TAGS + "]*)?)$", "gi");
	private final RegExp RELEASE_REGEX = RegExp.compile(RELEASE_SYMBOL + "[\\s]*([^" + TAGS + "]+)", "gi");
	private final RegExp EFFORT_REGEX = RegExp.compile(EFFORT_SYMBOL + "([\\d]+)(sp)?", "gi");

	// FIXME Delete MalformedScopeRepresentation
	public ScopeRepresentationParser(final String pattern) {
		final MatchResult matchResult = DESCRIPTION_REGEX.exec(removeUnusedSymbols(pattern + " "));
		if (matchResult == null) return;

		final String scopeRepresentation = matchResult.getGroup(1);
		if (scopeRepresentation != null) scopeDescription = scopeRepresentation.trim();

		final String tagsRepresentation = matchResult.getGroup(2);
		if (tagsRepresentation == null) return;

		extractReleaseDescription(tagsRepresentation);
		extractDeclaredEffort(tagsRepresentation);
	}

	public String getScopeDescription() {
		return scopeDescription == null ? "" : scopeDescription;
	}

	public String getReleaseDescription() {
		return releaseDescription == null ? "" : releaseDescription;
	}

	public int getDeclaredEffort() {
		return declaredEffort;
	}

	public boolean hasDeclaredEffort() {
		return hasDeclaredEffort;
	}

	private String removeUnusedSymbols(final String tagsRepresentation) {
		return tagsRepresentation.replace("\"", "").replace("'", "");
	}

	private void extractReleaseDescription(final String tagsRepresentation) {
		final MatchResult result = RELEASE_REGEX.exec(tagsRepresentation);
		releaseDescription = result.getGroup(1);
	}

	private void extractDeclaredEffort(final String tagsRepresentation) {
		final MatchResult result = EFFORT_REGEX.exec(tagsRepresentation);
		if (result == null) return;

		final Integer valueOfEffort = Integer.valueOf(result.getGroup(1));
		hasDeclaredEffort = (valueOfEffort != null);
		declaredEffort = valueOfEffort;
	}
}
