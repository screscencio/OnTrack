package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

import br.com.oncast.ontrack.shared.model.scope.exceptions.MalformedScopeException;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Parse a description and translate it into a scope description and a release description.
 */
public class ScopeRepresentationParser {

	private String scopeDescription;
	private String releaseDescription;

	private static final String STORY_PATTERN = "([^@]+)?";
	private static final String RELEASE_PATTERN = "(@[^@\\s\"]+)";
	private static final String RELEASE_PATTERN_WITH_QUOTES = "(@\"[^@]+\")";

	private final RegExp ONLY_ONE_RELEASE_AT_A_TIME_REGEX = RegExp.compile("(([^@])*@([^@])*){2,}", "gi");
	private final RegExp SCOPE_REG_EX = RegExp.compile(STORY_PATTERN + "(" + RELEASE_PATTERN + "|" + RELEASE_PATTERN_WITH_QUOTES + ")?", "gi");

	public ScopeRepresentationParser(final String scopeRepresentation) throws MalformedScopeException {
		scopeDescription = "";
		releaseDescription = "";

		if (scopeRepresentation.trim().equals("")) return;

		final MatchResult singleReleaseMatchResult = ONLY_ONE_RELEASE_AT_A_TIME_REGEX.exec(scopeRepresentation.trim());
		if (singleReleaseMatchResult != null) throw new MalformedScopeException("You cannot set more than one release at a time.");

		final MatchResult matchResult = SCOPE_REG_EX.exec(scopeRepresentation);

		setScopeDescription(matchResult.getGroup(1));
		setReleaseDescription(matchResult.getGroup(2));
	}

	private void setReleaseDescription(final String result) {
		if ((result != null) && (!result.equals(""))) {
			releaseDescription = formatRelease(result);
		}
	}

	/**
	 * Format a string, removing the at sign (@) and, if exists, quotes.
	 */
	private String formatRelease(final String unformated) {
		String formated = unformated.substring(1);

		if (unformated.contains("\"")) {
			formated = formated.replaceAll("\"", "");
		}

		return formated;
	}

	private void setScopeDescription(final String result) {
		if ((result == null) || (result.trim().equals(""))) throw new MalformedScopeException(
				"You have to set a scope description if you want to set a release.");

		scopeDescription = result.trim();
	}

	public String getScopeDescription() {
		return scopeDescription;
	}

	public String getReleaseDescription() {
		return releaseDescription;
	}
}
