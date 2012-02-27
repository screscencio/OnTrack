package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.EFFORT_SYMBOL;
import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.PROGRESS_SYMBOL;
import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.RELEASE_SYMBOL;
import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.VALUE_SYMBOL;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Parse a description and translate it into a scope description, release description, effort and progress.
 */
public class ScopeRepresentationParser {

	private String scopeDescription;
	private String releaseDescription;
	private float declaredEffort;
	private boolean hasDeclaredEffort;
	private float declaredValue;
	private boolean hasDeclaredValue;
	private String progressDescription;

	private static final String TAGS = StringRepresentationSymbols.getConcatenedSymbols();

	private final RegExp FULL_REGEX = RegExp.compile("^([\\s]*[^" + TAGS + "\\s].*?)([\\s]+[" + TAGS + "].+)*$", "gi");
	private final RegExp NODESCRIPTION_REGEX = RegExp.compile("^[\\s]*([" + TAGS + "].+)*$", "gi");

	private final RegExp RELEASE_REGEX = RegExp.compile(RELEASE_SYMBOL + "[\\s]*([^" + TAGS + "]+)", "gi");

	private final RegExp EFFORT_REGEX = RegExp.compile(EFFORT_SYMBOL + "[\\s]*((?:[\\d]+(?:\\.[\\d]*)?|\\.[\\d]+))(?:[es]p)?(?:\\s+.*)?$", "gi");

	private final RegExp VALUE_REGEX = RegExp.compile("\\" + VALUE_SYMBOL + "[\\s]*((?:[\\d]+(?:\\.[\\d]*)?|\\.[\\d]+))(?:[v]p)?(?:\\s+.*)?$", "gi");

	private final RegExp PROGRESS_REGEX = RegExp.compile(PROGRESS_SYMBOL + "[\\s]*([^" + TAGS + "]+)", "gi");

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

	public float getDeclaredEffort() {
		return declaredEffort;
	}

	public boolean hasDeclaredEffort() {
		return hasDeclaredEffort;
	}

	public boolean hasDeclaredValue() {
		return hasDeclaredValue;
	}

	public float getDeclaredValue() {
		return declaredValue;
	}

	public String getProgressDescription() {
		return progressDescription;
	}

	private String preparePattern(final String tagsRepresentation) {
		return tagsRepresentation;
	}

	private void interpretFullMatch(final String scopeRepresentation, final String tagsRepresentation) {
		if (scopeRepresentation != null) scopeDescription = scopeRepresentation.trim();

		interpretDescriptionMatch(tagsRepresentation);
	}

	private void interpretDescriptionMatch(final String tagsRepresentation) {
		if (tagsRepresentation == null) return;

		extractRelease(tagsRepresentation);
		extractDeclaredEffort(tagsRepresentation);
		extractDeclaredValue(tagsRepresentation);
		extractProgress(tagsRepresentation);
	}

	private void extractRelease(final String tagsRepresentation) {
		final MatchResult result = RELEASE_REGEX.exec(tagsRepresentation);
		if (result == null) return;

		final String stringResult = result.getGroup(1);
		releaseDescription = stringResult == null ? null : stringResult.trim();
	}

	private void extractDeclaredEffort(final String tagsRepresentation) {
		final MatchResult result = EFFORT_REGEX.exec(tagsRepresentation);
		if (result == null) return;

		final String stringResult = result.getGroup(1);
		final Float valueOfEffort = stringResult == null ? 0 : Float.valueOf(stringResult);

		hasDeclaredEffort = (stringResult != null);
		declaredEffort = valueOfEffort;
	}

	private void extractDeclaredValue(final String tagsRepresentation) {
		final MatchResult result = VALUE_REGEX.exec(tagsRepresentation);
		if (result == null) return;

		final String stringResult = result.getGroup(1);
		final Float valueOfValue = stringResult == null ? 0 : Float.valueOf(stringResult);

		hasDeclaredValue = (stringResult != null);
		declaredValue = valueOfValue;
	}

	private void extractProgress(final String tagsRepresentation) {
		final MatchResult result = PROGRESS_REGEX.exec(tagsRepresentation);
		if (result == null) return;

		final String stringResult = result.getGroup(1);
		progressDescription = stringResult == null ? null : stringResult.trim();
	}
}