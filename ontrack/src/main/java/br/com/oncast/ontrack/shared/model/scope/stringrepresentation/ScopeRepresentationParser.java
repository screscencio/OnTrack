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

	private final RegExp FULL_REGEX = RegExp.compile("^([\\s]*[^" + TAGS + "\\s].*?)([\\s]+[" + TAGS + "].+)*$", "gi");
	private final RegExp NODESCRIPTION_REGEX = RegExp.compile("^[\\s]*([" + TAGS + "].+)*$", "gi");

	private final RegExp RELEASE_REGEX = RegExp.compile(RELEASE_SYMBOL + "[\\s]*([^" + TAGS + "]+)", "gi");
	private final RegExp EFFORT_REGEX = RegExp.compile(EFFORT_SYMBOL + "([\\d]+)(?:[es]p)?(?:\\s+.*)?$", "gi");

	public ScopeRepresentationParser(final String pattern) {
		final String preparedPattern = preparePattern(pattern);

		MatchResult matchResult = FULL_REGEX.exec(preparedPattern);
		if (matchResult != null) interpretFullMatch(matchResult.getGroup(1), matchResult.getGroup(2));
		else {
			matchResult = NODESCRIPTION_REGEX.exec(preparedPattern);
			if (matchResult == null) return;
			interpretDescriptionMatch(matchResult.getGroup(1));
		}
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

	private String preparePattern(final String tagsRepresentation) {
		return tagsRepresentation.replace("\"", "").replace("'", "");
	}

	private void interpretFullMatch(final String scopeRepresentation, final String tagsRepresentation) {
		if (scopeRepresentation != null) scopeDescription = scopeRepresentation.trim();

		interpretDescriptionMatch(tagsRepresentation);
	}

	private void interpretDescriptionMatch(final String tagsRepresentation) {
		if (tagsRepresentation == null) return;

		extractReleaseDescription(tagsRepresentation);
		extractDeclaredEffort(tagsRepresentation);
	}

	private void extractReleaseDescription(final String tagsRepresentation) {
		final MatchResult result = RELEASE_REGEX.exec(tagsRepresentation);
		if (result == null) return;

		final String stringResult = result.getGroup(1);
		releaseDescription = stringResult == null ? null : stringResult.trim();
	}

	private void extractDeclaredEffort(final String tagsRepresentation) {
		final MatchResult result = EFFORT_REGEX.exec(tagsRepresentation);
		if (result == null) return;

		final String stringResult = result.getGroup(1);
		final Integer valueOfEffort = stringResult == null ? 0 : Integer.valueOf(stringResult);

		hasDeclaredEffort = (stringResult != null);
		declaredEffort = valueOfEffort;
	}
}
