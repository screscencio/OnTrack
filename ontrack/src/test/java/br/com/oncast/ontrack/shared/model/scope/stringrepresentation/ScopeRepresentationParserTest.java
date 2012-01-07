package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.EFFORT_SYMBOL;
import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.PROGRESS_SYMBOL;
import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.RELEASE_SYMBOL;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScopeRepresentationParserTest {

	private static final String ALLOWED_SYMBOLS;

	static {
		final String allSymbols = "!@#$%¨&*()-_=+{[}]^~<,>.:;?/\\|";
		final String[] reservedSymbols = StringRepresentationSymbols.SYMBOLS;

		String testSymbols = allSymbols;
		for (final String symbol : reservedSymbols) {
			testSymbols = testSymbols.replace(symbol, "");
		}

		ALLOWED_SYMBOLS = testSymbols;
	}

	@Test
	public void shouldMatchDescription() {
		test("descrição", "descrição", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionContainingEmail() {
		test("descrição de email@provedor.com.br", "descrição de email@provedor.com.br", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionWithSpaces() {
		test("descrição bla", "descrição bla", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndTrimIt1() {
		test("descrição bla ", "descrição bla", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndTrimIt2() {
		test("  descrição bla", "descrição bla", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndNotIgnoreQuotes1() {
		test("descrição\" bla", "descrição\" bla", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndNotIgnoreQuotes2() {
		test("descrição' bla", "descrição' bla", "", false, 0);
	}

	@Test
	public void shouldMatchEmptyDescription() {
		test("", "", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndRelease() {
		test("descrição " + RELEASE_SYMBOL + "release/subrelease", "descrição", "release/subrelease", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseWithSpacesBetweenReleases() {
		test("descrição " + RELEASE_SYMBOL + "release /subrelease", "descrição", "release /subrelease", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseWithSpacesBetweenReleases2() {
		test("descrição " + RELEASE_SYMBOL + "release / subrelease", "descrição", "release / subrelease", false, 0);
	}

	@Test
	public void shouldMatchDescriptionContainingEmailAndRelease() {
		test("descrição email@provedor.com.br " + RELEASE_SYMBOL + "release/subrelease", "descrição email@provedor.com.br", "release/subrelease", false, 0);
	}

	@Test
	public void shouldMatchReleaseOnly() {
		test(RELEASE_SYMBOL + "release", "", "release", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSpace() {
		test(" " + RELEASE_SYMBOL + "release", "", "release", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSpaces() {
		test(RELEASE_SYMBOL + "release com espaços", "", "release com espaços", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSpacesAndTrimIt1() {
		test(RELEASE_SYMBOL + "release com espaços ", "", "release com espaços", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSpacesAndTrimIt2() {
		test(RELEASE_SYMBOL + " release com espaços ", "", "release com espaços", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSpacesAndTrimIt3() {
		test(RELEASE_SYMBOL + "release \t", "", "release", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSpacesAndTrimIt4() {
		test(RELEASE_SYMBOL + "   release", "", "release", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithNoText() {
		test(" " + RELEASE_SYMBOL + "", "", "", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithNoTextAndWithOnlyWhiteSpaces() {
		test(" " + RELEASE_SYMBOL + "        ", "", "", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSeparatorAtStart() {
		test(" " + RELEASE_SYMBOL + "/release", "", "/release", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSeparatorAtTheEnd() {
		test(" " + RELEASE_SYMBOL + "release/", "", "release/", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSeparatorsDisposedInAnyWay() {
		test(" " + RELEASE_SYMBOL + "release//another release", "", "release//another release", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSeparatorsDisposedInAnyWay2() {
		test(" " + RELEASE_SYMBOL + "/release/ //another release//", "", "/release/ //another release//", false, 0);
	}

	@Test
	public void shouldMatchReleaseAndNotIgnoreQuotes1() {
		test(RELEASE_SYMBOL + "re\"lease", "", "re\"lease", false, 0);
	}

	@Test
	public void shouldMatchReleaseAndNotIgnoreQuotes2() {
		test(RELEASE_SYMBOL + "re'lease", "", "re'lease", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSpacesAndNotIgnoreQuotes() {
		test(RELEASE_SYMBOL + "rele'ase com es\"paços", "", "rele'ase com es\"paços", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseWithSpacesAndNotIgnoreQuotes() {
		test("desc\"rição com espaços " + RELEASE_SYMBOL + "rele'ase com es\"paços", "desc\"rição com espaços", "rele'ase com es\"paços", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseWithSimbols() {
		test("inicio" + ALLOWED_SYMBOLS + "fim " + RELEASE_SYMBOL + "release" + ALLOWED_SYMBOLS, "inicio" + ALLOWED_SYMBOLS + "fim", "release"
				+ ALLOWED_SYMBOLS, false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseWithSimbolsAndSpacesAndNumbers1() {
		test("descrição com " + ALLOWED_SYMBOLS + " de monte " + RELEASE_SYMBOL + "release com 3 espaços", "descrição com " + ALLOWED_SYMBOLS + " de monte",
				"release com 3 espaços", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseWithSimbolsAndSpacesAndNumbers2() {
		test("descrição com 3 espaços " + RELEASE_SYMBOL + "release com " + ALLOWED_SYMBOLS + " de monte", "descrição com 3 espaços", "release com "
				+ ALLOWED_SYMBOLS + " de monte", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndEffort1() {
		test("descrição " + EFFORT_SYMBOL + "1", "descrição", "", true, 1);
	}

	@Test
	public void shouldMatchDescriptionAndEffort2() {
		test("descrição " + EFFORT_SYMBOL + "99", "descrição", "", true, 99);
	}

	@Test
	public void shouldMatchDescriptionAndEffort3() {
		test("descrição " + EFFORT_SYMBOL + "21sp", "descrição", "", true, 21);
	}

	@Test
	public void shouldMatchDescriptionAndEffort4() {
		test("descrição " + EFFORT_SYMBOL + "21ep", "descrição", "", true, 21);
	}

	@Test
	public void shouldMatchDescriptionAndEffort5() {
		test("descrição " + EFFORT_SYMBOL + "21 dfsdfsdep", "descrição", "", true, 21);
	}

	@Test
	public void shouldMatchEffortWithSpacesAndTrimIt1() {
		test(EFFORT_SYMBOL + "   33", "", "", true, 33);
	}

	@Test
	public void shouldMatchEffortWithSpacesAndTrimIt2() {
		test(" " + EFFORT_SYMBOL + "   33 ", "", "", true, 33);
	}

	@Test
	public void shouldMatchReleaseAndEffort1() {
		test(RELEASE_SYMBOL + "release/subrelease " + EFFORT_SYMBOL + "1", "", "release/subrelease", true, 1);
	}

	@Test
	public void shouldMatchReleaseAndEffort2() {
		test(" " + RELEASE_SYMBOL + "release/subrelease " + EFFORT_SYMBOL + "1 ", "", "release/subrelease", true, 1);
	}

	@Test
	public void shouldMatchDescriptionAndNoEffort() {
		test("descrição " + EFFORT_SYMBOL + "21blabla", "descrição", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndNoEffortAndRelease() {
		test("descrição " + EFFORT_SYMBOL + "21blabla " + RELEASE_SYMBOL + "release", "descrição", "release", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndEffortAndReleaseDespiteOrder() {
		test("descrição " + EFFORT_SYMBOL + "21sp " + RELEASE_SYMBOL + "release", "descrição", "release", true, 21);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseAndEffortWithIgnoredCharacters() {
		test("descrição " + EFFORT_SYMBOL + "21 dfsdfsdep" + RELEASE_SYMBOL + "release", "descrição", "release", true, 21);
	}

	@Test
	public void shouldMatchDescriptionAndEffortAndRelease() {
		test("descrição " + EFFORT_SYMBOL + "21 " + RELEASE_SYMBOL + "release", "descrição", "release", true, 21);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseAndEffort() {
		test("descrição " + RELEASE_SYMBOL + "release" + EFFORT_SYMBOL + "21 ", "descrição", "release", true, 21);
	}

	@Test
	public void shouldMatchEffortOnly() {
		test(EFFORT_SYMBOL + "21", "", "", true, 21);
	}

	@Test
	public void shouldMatchFloatingEffort() {
		test(EFFORT_SYMBOL + "21.3", "", "", true, 21.3F);
	}

	@Test
	public void shouldNotMatchPointAsEffort() {
		test(EFFORT_SYMBOL + ".", "", "", false, 0);
	}

	@Test
	public void shouldMatchUncompleteFloatingEffort() {
		test(EFFORT_SYMBOL + "21.", "", "", true, 21.0F);
	}

	@Test
	public void shouldNotMatchUncompleteFloatingEffortOnly2() {
		test(EFFORT_SYMBOL + ".5", "", "", true, 0.5F);
	}

	@Test
	public void shouldMatchEffortOnlyWithSufixSp() {
		test(EFFORT_SYMBOL + "21.2sp", "", "", true, 21.2F);
	}

	@Test
	public void shouldMatchEffortOnlyWithSufixEp() {
		test(EFFORT_SYMBOL + "21ep", "", "", true, 21);
	}

	@Test
	public void shouldNotMatchEffortOrAnything() {
		test(EFFORT_SYMBOL + "21epaaa Descrição", "", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndProgress() {
		test("descrição " + PROGRESS_SYMBOL + "Not started", "descrição", "Not started");
	}

	@Test
	public void shouldMatchDescriptionContainingPercentageAndProgress() {
		test("descrição 100% " + PROGRESS_SYMBOL + "NS", "descrição 100%", "NS");
	}

	@Test
	public void shouldMatchProgressOnly() {
		test(PROGRESS_SYMBOL + "ns", "", "ns");
	}

	@Test
	public void shouldMatchProgressWithSpace() {
		test(" " + PROGRESS_SYMBOL + "n", "", "n");
	}

	@Test
	public void shouldMatchProgressWithSpaces() {
		test(PROGRESS_SYMBOL + "under work", "", "under work");
	}

	@Test
	public void shouldMatchProgressWithSpacesAndTrimIt1() {
		test(PROGRESS_SYMBOL + "Under work ", "", "Under work");
	}

	@Test
	public void shouldMatchProgressWithSpacesAndTrimIt2() {
		test(PROGRESS_SYMBOL + " Under work ", "", "Under work");
	}

	@Test
	public void shouldMatchProgressWithSpacesAndTrimIt3() {
		test(PROGRESS_SYMBOL + "Under work \t", "", "Under work");
	}

	@Test
	public void shouldMatchProgressWithSpacesAndTrimIt4() {
		test(PROGRESS_SYMBOL + "   Under work", "", "Under work");
	}

	@Test
	public void shouldMatchProgressAndNotIgnoreQuotes1() {
		test(PROGRESS_SYMBOL + "Under\" work", "", "Under\" work");
	}

	@Test
	public void shouldMatchProgressAndNotIgnoreQuotes2() {
		test(PROGRESS_SYMBOL + "Under'work", "", "Under'work");
	}

	@Test
	public void shouldMatchProgressWithSpacesAndNotIgnoreQuotes() {
		test(PROGRESS_SYMBOL + "I am 'design\"ing", "", "I am 'design\"ing");
	}

	@Test
	public void shouldMatchDescriptionAndProgressWithSpacesAndNotIgnoreQuotes() {
		test("desc\"rição com espaços " + PROGRESS_SYMBOL + "I am 'design\"ing", "desc\"rição com espaços", "I am 'design\"ing");
	}

	@Test
	public void shouldMatchDescriptionAndProgressWithSimbols() {
		test("inicio" + ALLOWED_SYMBOLS + "fim " + PROGRESS_SYMBOL + "done" + ALLOWED_SYMBOLS, "inicio" + ALLOWED_SYMBOLS + "fim", "done" + ALLOWED_SYMBOLS);
	}

	@Test
	public void shouldMatchDescriptionAndProgressWithSimbolsAndSpacesAndNumbers1() {
		test("descrição com " + ALLOWED_SYMBOLS + " de monte " + PROGRESS_SYMBOL + "progresso com 3 espaços", "descrição com " + ALLOWED_SYMBOLS + " de monte",
				"progresso com 3 espaços");
	}

	@Test
	public void shouldMatchDescriptionAndProgressWithSimbolsAndSpacesAndNumbers2() {
		test("descrição com 3 espaços " + PROGRESS_SYMBOL + "progresso com " + ALLOWED_SYMBOLS + " de monte", "descrição com 3 espaços", "progresso com "
				+ ALLOWED_SYMBOLS + " de monte");
	}

	@Test
	public void shouldMatchProgressDiscardingProgressSymbolInsideTheProgressDescription() {
		test(PROGRESS_SYMBOL + "I am about 80% done", "", "I am about 80");
	}

	@Test
	public void shouldMatchScopeDescriptionAndReleaseAndEffortAndProgress() {
		test("descrição " + RELEASE_SYMBOL + "release" + EFFORT_SYMBOL + "21 " + PROGRESS_SYMBOL + "Not started ", "descrição", "release", "Not started", true,
				21);
	}

	private void test(final String pattern, final String descriptionMatch, final String releaseMatch, final boolean hasDeclaredEffort,
			final float declaredEffort) {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(pattern);
		assertEquals(descriptionMatch, parser.getScopeDescription());
		assertEquals(releaseMatch, parser.getReleaseDescription());
		assertEquals(hasDeclaredEffort, parser.hasDeclaredEffort());
		assertEquals(declaredEffort, parser.getDeclaredEffort(), 0);
	}

	private void test(final String pattern, final String descriptionMatch, final String progressMatch) {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(pattern);
		assertEquals(descriptionMatch, parser.getScopeDescription());
		assertEquals(progressMatch, parser.getProgressDescription());
	}

	private void test(final String pattern, final String descriptionMatch, final String releaseMatch, final String progressMatch,
			final boolean hasDeclaredEffort, final float declaredEffort) {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(pattern);
		assertEquals(descriptionMatch, parser.getScopeDescription());
		assertEquals(releaseMatch, parser.getReleaseDescription());
		assertEquals(hasDeclaredEffort, parser.hasDeclaredEffort());
		assertEquals(declaredEffort, parser.getDeclaredEffort(), 0);
		assertEquals(progressMatch, parser.getProgressDescription());
	}

}
