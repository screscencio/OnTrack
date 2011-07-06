package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

import static br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbols.EFFORT_SYMBOL;
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
	public void shouldMatchDescriptionAndIgnoreQuotes1() {
		test("descrição\" bla", "descrição bla", "", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndIgnoreQuotes2() {
		test("descrição' bla", "descrição bla", "", false, 0);
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
	public void shouldMatchDescriptionContainingEmailAndRelease() {
		test("descrição email@provedor.com.br " + RELEASE_SYMBOL + "release/subrelease", "descrição email@provedor.com.br", "release/subrelease", false, 0);
	}

	@Test
	public void shouldMatchReleaseOnly() {
		test(RELEASE_SYMBOL + "release", "", "release", false, 0);
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
	public void shouldMatchReleaseAndIgnoreQuotes1() {
		test(RELEASE_SYMBOL + "re\"lease", "", "release", false, 0);
	}

	@Test
	public void shouldMatchReleaseAndIgnoreQuotes2() {
		test(RELEASE_SYMBOL + "re'lease", "", "release", false, 0);
	}

	@Test
	public void shouldMatchReleaseWithSpacesAndIgnoreQuotes() {
		test(RELEASE_SYMBOL + "rele'ase com es\"paços", "", "release com espaços", false, 0);
	}

	@Test
	public void shouldMatchDescriptionAndReleaseWithSpacesAndIgnoreQuotes() {
		test("desc\"rição com espaços " + RELEASE_SYMBOL + "rele'ase com es\"paços", "descrição com espaços", "release com espaços", false, 0);
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

	private void test(final String pattern, final String descriptionMatch, final String releaseMatch, final boolean hasDeclaredEffort, final int declaredEffort) {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(pattern);
		assertEquals(descriptionMatch, parser.getScopeDescription());
		assertEquals(releaseMatch, parser.getReleaseDescription());
		assertEquals(hasDeclaredEffort, parser.hasDeclaredEffort());
		assertEquals(declaredEffort, parser.getDeclaredEffort());
	}

}
