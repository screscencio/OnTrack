package br.com.oncast.ontrack.shared.scope.pattern;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class ScopePatternParser {

	private final static RegExp SCOPE_REG_EX = RegExp.compile("([^@!]+)|(@\"[^@]+\")|(@[^@\\s]+)", "gi");

	private final String scopeDescription;
	private final String releaseDescription;

	public ScopePatternParser(final String newPattern) {
		final MatchResult matchResult = SCOPE_REG_EX.exec(newPattern);
		final int groupCount = matchResult.getGroupCount();
		for (int i = 0; i < groupCount; i++) {
			System.out.println("group " + i + ": " + matchResult.getGroup(i));
		}
		scopeDescription = "";
		releaseDescription = "";
	}

	public String getScopeDescription() {
		return scopeDescription;
	}

	public String getReleaseDescription() {
		return releaseDescription;
	}
}
