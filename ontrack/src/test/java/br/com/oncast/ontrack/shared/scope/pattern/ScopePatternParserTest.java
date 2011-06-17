package br.com.oncast.ontrack.shared.scope.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.scope.exceptions.MalformedScopeException;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationParser;

public class ScopePatternParserTest {

	private static final String STORY = "Isto é uma história";
	private static final String SPACE = " ";
	private static final String RELEASE = "@R1";
	private static final String RELEASE_EXPECTED = "R1";
	private static final String RELEASE_WITH_QUOTES = "@\"Release 1\"";
	private static final String RELEASE_WITH_QUOTES_EXPECTED = "Release 1";
	private static final String RELEASE_PLUS_ITERATION = "@R1/It1";
	private static final String RELEASE_PLUS_ITERATION_EXPECTED = "R1/It1";

	@Test
	public void shouldMatchScope() {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(STORY);

		assertEquals(STORY, parser.getScopeDescription());
		assertTrue(parser.getReleaseDescription().isEmpty());
	}

	@Test
	public void shouldMatchScopeAndRelease() {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(STORY + SPACE + RELEASE);

		assertEquals(STORY, parser.getScopeDescription());
		assertEquals(RELEASE_EXPECTED, parser.getReleaseDescription());
	}

	@Test
	public void shouldMatchScopeAndReleaseWithQuotes() {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(STORY + SPACE + RELEASE_WITH_QUOTES);

		assertEquals(STORY, parser.getScopeDescription());
		assertEquals(RELEASE_WITH_QUOTES_EXPECTED, parser.getReleaseDescription());
	}

	@Test
	public void shouldMatchScopeAndReleaseAndIteration() {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(STORY + SPACE + RELEASE_PLUS_ITERATION);

		assertEquals(STORY, parser.getScopeDescription());
		assertEquals(RELEASE_PLUS_ITERATION_EXPECTED, parser.getReleaseDescription());
	}

	@Test
	public void shouldMatchAnEmptyScope() {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser("");

		assertEquals("", parser.getScopeDescription());
		assertTrue(parser.getReleaseDescription().isEmpty());
	}

	@Test
	public void shouldMatchScopeWithSpaceInItsDescription() {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(" ");

		assertEquals("", parser.getScopeDescription());
		assertTrue(parser.getReleaseDescription().isEmpty());
	}

	@Test
	public void shouldMatchAScopeWithSymbolsInItsDescription() {
		final String symbols = "!?*&%#$()-_+=[]{}\\/,.:;<>~^'`ªº°¹²³£¢¬§";
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(STORY + symbols + SPACE + RELEASE);

		assertEquals(STORY + symbols, parser.getScopeDescription());
		assertEquals(RELEASE_EXPECTED, parser.getReleaseDescription());
	}

	@Test
	public void shouldMatchAScopeWithNumbersInItsDescription() {
		final String numbers = "1234567890";
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(STORY + numbers + SPACE + RELEASE);

		assertEquals(STORY + numbers, parser.getScopeDescription());
		assertEquals(RELEASE_EXPECTED, parser.getReleaseDescription());
	}

	@Test(expected = MalformedScopeException.class)
	public void shouldNotAcceptMoreThanOneReleaseAtSameTime() {
		new ScopeRepresentationParser(STORY + SPACE + RELEASE + SPACE + RELEASE_WITH_QUOTES);
	}

	@Test(expected = MalformedScopeException.class)
	public void shouldNotAcceptMoreThanOneReleaseAtSameTime_Variation() {
		new ScopeRepresentationParser(STORY + SPACE + RELEASE_WITH_QUOTES + SPACE + RELEASE);
	}

	@Test(expected = MalformedScopeException.class)
	public void shouldNotAcceptMoreThanOneReleaseAtSameTime_Variation2() {
		new ScopeRepresentationParser(STORY + SPACE + "@@R1 @Teste");
	}

	@Test(expected = MalformedScopeException.class)
	public void shouldConsiderOrder() {
		new ScopeRepresentationParser(RELEASE + SPACE + STORY);
	}

	@Test(expected = MalformedScopeException.class)
	public void shouldNotAcceptAReleaseWithoutAScope() {
		new ScopeRepresentationParser(SPACE + RELEASE);
	}

	@Test(expected = MalformedScopeException.class)
	public void shouldNotAcceptAReleaseWithoutAScope_Variation() {
		new ScopeRepresentationParser(RELEASE);
	}
}
