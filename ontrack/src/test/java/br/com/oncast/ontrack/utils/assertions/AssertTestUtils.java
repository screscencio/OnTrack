package br.com.oncast.ontrack.utils.assertions;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class AssertTestUtils {

	private static final double EFFORT_TOLERANCE = 0.09;

	public static void assertDeepEquals(final Scope expected, final Scope actual) {
		assertEquality(expected, actual);
		for (int i = 0; i < actual.getChildren().size(); i++) {
			assertDeepEquals(expected.getChild(i), actual.getChild(i));
		}
	}

	private static void assertEquality(final Scope expected, final Scope actual) {
		assertTrue("Checking equality of scope '" + actual.getDescription() + "'.", actual.getDescription().equals(expected.getDescription()));
		assertEquality("Checking equality of effort of scope '" + actual.getDescription() + "'.", expected.getEffort(), actual.getEffort());
	}

	public static void assertEquality(final String message, final Effort expected, final Effort actual) {
		assertEquals(message, expected.getBottomUpValue(), actual.getBottomUpValue(), EFFORT_TOLERANCE);
		assertEquals(message, expected.getTopDownValue(), actual.getTopDownValue(), EFFORT_TOLERANCE);
		assertEquals(message, expected.hasDeclared(), actual.hasDeclared());
		assertEquals(message, expected.getDeclared(), actual.getDeclared(), EFFORT_TOLERANCE);
	}

	public static void assertNotEquals(final Object expected, final Object actual) {
		assertFalse(expected.equals(actual));
	}

	public static <T> void assertNotContains(final T unexpected, final Collection<T> actual) {
		assertFalse(actual.contains(unexpected));
	}

	public static <T> void assertContainsNone(final Collection<T> unexpected, final Collection<T> actual) {
		for (final T t : unexpected) {
			assertFalse(actual.contains(t));
		}
	}

	public static <T> void assertCollectionEquality(final Collection<T> expected, final Collection<T> actual) {
		assertEquals(expected.size(), actual.size());
		assertContainsAll(actual, expected);
	}

	public static <T> void assertContainsAll(final Collection<T> actual, final T... expected) {
		assertContainsAll(actual, Arrays.asList(expected));
	}

	public static <T> void assertContainsAll(final Collection<T> actual, final Collection<T> expected) {
		assertTrue(actual.containsAll(expected));
	}
}
