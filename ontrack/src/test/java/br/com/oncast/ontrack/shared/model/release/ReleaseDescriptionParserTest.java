package br.com.oncast.ontrack.shared.model.release;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertNotEquals;

public class ReleaseDescriptionParserTest {

	private static final String SEPARATOR = "/";
	private static final int TEST_LENGTH = 5;
	private static final char[] WHITE_CHARACTERS = { ' ', '\n', '\r', '\t' };

	@Test
	public void emptyStringShouldBeTheHeadOfANullDescription() {
		assertEquals("", getHeadOfDescription(null));
	}

	@Test
	public void emptyStringShouldBeTheHeadOfABlankDescription() {
		for (final String description : getWhiteSpaceConcatenationVariationsOf("")) {
			assertEquals("", getHeadOfDescription(description));
		}
	}

	@Test
	public void emptyStringShouldBeTheHeadOfADescriptionContainningOnlyTheSeparatorAndWhiteSpaces() throws Exception {
		for (final String description : getWhiteSpaceConcatenationVariationsOf(SEPARATOR)) {
			assertEquals("", getHeadOfDescription(description));
		}
	}

	@Test
	public void emptyStringShouldBeTheHeadOfADescriptionContainningManySeparatorsAndWhiteSpaces() throws Exception {
		final String[] descriptionWithSeparators = { " /  / ", "//", " // ", " / / ", " //////", "//     //" };
		for (final String description : descriptionWithSeparators) {
			assertEquals("", getHeadOfDescription(description));
		}
	}

	@Test
	public void theTrimOfTheReleaseShouldBeTheHeadOfADescriptionWithSingleRelease() {
		for (final String description : getWhiteSpaceConcatenationVariationsOf("R1")) {
			assertEquals("R1", getHeadOfDescription(description));
		}
	}

	@Test
	public void theHeadOfOtherSingleReleaseDescriptionIsTheTrimOfHimself() {
		for (final String description : getWhiteSpaceConcatenationVariationsOf("R2")) {
			assertEquals("R2", getHeadOfDescription(description));
		}
	}

	@Test
	public void theTrimOfTheSecondReleaseShouldBeTheHeadWhenTheFirstReleaseIsInvalid() throws Exception {
		for (final String invalidFirstRelease : getWhiteSpaceConcatenationVariationsOf("")) {
			for (final String description : getWhiteSpaceConcatenationVariationsOf(invalidFirstRelease + SEPARATOR + "R2")) {
				assertEquals("R2", getHeadOfDescription(description));
			}
		}
	}

	@Test
	public void firstReleaseShouldHasNextWhenItHasAChildRelease() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + "R2");
		assertTrue(parser.hasNext());
	}

	@Test
	public void firstReleaseShouldNotHasNextWhenItDoesNotHaveAChildRelease() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1");
		assertFalse(parser.hasNext());
	}

	@Test
	public void hasNextIsFalseAfterCallingNextInAReleaseHierarchyWith2Levels() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + "R2");
		assertTrue(parser.next());
		assertFalse(parser.hasNext());
	}

	@Test
	public void theFirstReleaseShouldBeTheHeadBeforeNextIsCalledWhenTheDescriptionHasTwoLevels() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + "R2");
		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.next());
		assertNotEquals("R1", parser.getHeadRelease());
	}

	@Test
	public void theSecondReleaseShouldBeTheHeadAfterNextIsCalledAndTheDescriptionHasTwoLevels() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + "R2");
		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.next());
		assertEquals("R2", parser.getHeadRelease());
		assertEquals("R1" + SEPARATOR + "R2", parser.getFullDescriptionOfHeadRelease());
		for (int i = 0; i < TEST_LENGTH; i++) {
			assertFalse(parser.next());
			assertEquals("", parser.getHeadRelease());
		}
	}

	@Test
	public void fullDescriptionOfSubReleaseShouldBeTheEntireReleaseHierarchyUntilTheActualRelease() throws Exception {
		final String fullReleaseDescription = "R1" + SEPARATOR + "It1" + SEPARATOR + "W1";
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(fullReleaseDescription);
		assertTrue(parser.next());
		assertTrue(parser.next());
		assertEquals(fullReleaseDescription, parser.getFullDescriptionOfHeadRelease());
	}

	@Test
	public void headShoudIgnoreTwoSeparatorWithOnlySpacesBetween() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + " " + SEPARATOR + "R2");
		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.next());
		assertEquals("R2", parser.getHeadRelease());
		assertEquals("R1" + SEPARATOR + "R2", parser.getFullDescriptionOfHeadRelease());
		for (int i = 0; i < TEST_LENGTH; i++) {
			assertFalse(parser.next());
			assertEquals("", parser.getHeadRelease());
		}
	}

	@Test
	public void headShoudIgnoreTwoSeparatorWithNoTextBetween() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + SEPARATOR + "R2");
		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.next());
		assertEquals("R2", parser.getHeadRelease());
		assertEquals("R1" + SEPARATOR + "R2", parser.getFullDescriptionOfHeadRelease());
		for (int i = 0; i < TEST_LENGTH; i++) {
			assertFalse(parser.next());
			assertEquals("", parser.getHeadRelease());
		}
	}

	@Test
	public void headShoudIgnoreSeparatorAtTheBeginning() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(SEPARATOR + "R1");

		assertEquals("R1", parser.getHeadRelease());
		assertFalse(parser.hasNext());
	}

	@Test
	public void headShoudIgnoreSeparatorsAtTheBeginning() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(SEPARATOR + SEPARATOR + "R1");

		assertEquals("R1", parser.getHeadRelease());
		assertFalse(parser.hasNext());
	}

	@Test
	public void headShoudIgnoreManySeparatorsAtTheBeginning() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(SEPARATOR + SEPARATOR + SEPARATOR + "R1" + SEPARATOR + "R2");

		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.hasNext());
	}

	@Test
	public void headShoudIgnoreManySeparatorsAtTheBeginningWithTwoReleasesInHierarchy() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(SEPARATOR + SEPARATOR + SEPARATOR + "R1" + SEPARATOR + "R2");

		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.hasNext());

		parser.next();

		assertEquals("R2", parser.getHeadRelease());
		assertFalse(parser.hasNext());
	}

	@Test
	public void headShoudIgnoreManySeparatorsAtTheBeginningAndAtTheEndWithTwoReleasesInHierarchy() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(
				SEPARATOR + SEPARATOR + SEPARATOR + "R1" + SEPARATOR + "R2" + SEPARATOR + SEPARATOR);

		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.hasNext());

		parser.next();

		assertEquals("R2", parser.getHeadRelease());
		assertFalse(parser.hasNext());
	}

	@Test
	public void headShoudIgnoreManySeparatorsAtTheBeginningAndInMiddleAndAtTheEndWithTwoReleasesInHierarchy() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(
				SEPARATOR + SEPARATOR + SEPARATOR + "R1" + SEPARATOR + SEPARATOR + SEPARATOR + "R2" + SEPARATOR + SEPARATOR);

		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.hasNext());

		parser.next();

		assertEquals("R2", parser.getHeadRelease());
		assertFalse(parser.hasNext());
	}

	@Test
	public void headShoudIgnoreManySeparatorsWithSpacesBetweenAtTheBeginningAndInMiddleAndAtTheEndWithTwoReleasesInHierarchy() throws Exception {
		final String threeSeparatorsWithSpaces = "   " + SEPARATOR + " " + SEPARATOR + " " + SEPARATOR + "     ";
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(
				threeSeparatorsWithSpaces + "R1" + threeSeparatorsWithSpaces + "R2" + threeSeparatorsWithSpaces);

		assertEquals("R1", parser.getHeadRelease());
		assertTrue(parser.hasNext());

		parser.next();

		assertEquals("R2", parser.getHeadRelease());
		assertFalse(parser.hasNext());
	}

	@Test
	public void headShoudIgnoreSeparatorAtTheEnd() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR);
		assertEquals("R1", parser.getHeadRelease());
	}

	@Test
	public void headShoudIgnoreSeparatorsAtTheEnd() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + SEPARATOR);
		assertEquals("R1", parser.getHeadRelease());
	}

	@Test
	public void separatorAtTheEndShouldNotBeConsideredAsNext() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR);
		assertFalse(parser.hasNext());
	}

	@Test
	public void separatorsAtTheEndShouldNotBeConsideredAsNext() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + SEPARATOR);
		assertFalse(parser.hasNext());
	}

	@Test
	public void threeSeparatorsAtTheEndShouldNotBeConsideredAsNext() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1" + SEPARATOR + SEPARATOR + SEPARATOR);
		assertFalse(parser.hasNext());
	}

	@Test
	public void threeSeparatorsAtTheEndShouldNotBeConsideredAsNext_WithSpacesBetweenThen() throws Exception {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser("R1 " + SEPARATOR + " " + SEPARATOR + " " + SEPARATOR);
		assertFalse(parser.hasNext());
	}

	@Test
	public void TheTrimOfTheFirstReleaseShouldBeTheHeadOfADescriptionWithMoreThanOneLevelOfReleases() {
		for (final String description : getWhiteSpaceConcatenationVariationsOf("R1" + SEPARATOR + "R2")) {
			assertEquals("R1", getHeadOfDescription(description));
		}
	}

	@Test
	public void theTailOfABlankDescriptionShouldBeAEmptyString() throws Exception {
		for (final String description : getWhiteSpaceConcatenationVariationsOf("")) {
			assertEquals("", getTailOfDescription(description));
		}
	}

	@Test
	public void theTailOfADescriptionContainningOnlyTheSeparatorAndWhiteSpacesShouldBeAEmptyString() throws Exception {
		for (final String description : getWhiteSpaceConcatenationVariationsOf(SEPARATOR)) {
			assertEquals("", getTailOfDescription(description));
		}
	}

	@Test
	public void tailOfADescriptionWithSingleReleaseShouldBeAnEmptyString() throws Exception {
		for (final String description : getWhiteSpaceConcatenationVariationsOf("R1")) {
			assertEquals("", getTailOfDescription(description));
		}
	}

	@Test
	public void theTailOfADescriptionWithTwoLevelsOfReleasesShouldBeTheTrimOfTheSecondRelease() throws Exception {
		for (final String secondRelease : getWhiteSpaceConcatenationVariationsOf("R2")) {
			assertEquals("R2", getTailOfDescription("R1" + SEPARATOR + secondRelease));
		}
	}

	@Test
	public void theTailOfADescriptionWithMoreThanOneLevelOfReleasesShouldBeATrimOfTheStringWithAllReleasesAfterTheFirstRelease() throws Exception {
		for (final String description : getWhiteSpaceConcatenationVariationsOf("R1/R2/R3")) {
			assertEquals("R2/R3", getTailOfDescription(description));
		}
	}

	@Test
	public void theNextTailOfADescriptionWithMoreThanOneLevelOfReleasesShouldBeATrimOfTheStringWithAllReleasesAfterTheSecondRelease() throws Exception {
		for (final String description : getWhiteSpaceConcatenationVariationsOf("R1/R2/R3")) {
			final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(description);
			assertEquals("R2/R3", parser.getTailReleases());
			parser.next();
			assertEquals("R3", parser.getTailReleases());
		}
	}

	private String getTailOfDescription(final String description) {
		return new ReleaseDescriptionParser(description).getTailReleases();
	}

	private String getHeadOfDescription(final String description) {
		return new ReleaseDescriptionParser(description).getHeadRelease();
	}

	private List<String> getWhiteSpaceConcatenationVariationsOf(final String string) {
		final ArrayList<String> list = new ArrayList<String>();
		list.add(string);
		for (final char character : WHITE_CHARACTERS) {
			list.add(character + string);
			list.add(character + string + character);
			list.add(string + character);
		}
		return list;
	}
}
