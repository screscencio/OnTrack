package br.com.oncast.ontrack.utils.assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class AssertTestUtils {

	public static void assertDeepEquals(final Scope actual, final Scope expected) {
		assertEquality(actual, expected);
		for (int i = 0; i < actual.getChildren().size(); i++) {
			assertDeepEquals(actual.getChild(i), expected.getChild(i));
		}
	}

	private static void assertEquality(final Scope actual, final Scope expected) {
		assertTrue("Checking equality of scope '" + actual.getDescription() + "'.", actual.getDescription().equals(expected.getDescription()));
		assertEquality("Checking equality of effort of scope '" + actual.getDescription() + "'.", actual.getEffort(), expected.getEffort());
	}

	public static void assertEquality(final String message, final Effort actual, final Effort expected) {
		assertEquals(message, expected.getBottomUpValue(), actual.getBottomUpValue(), 0.09);
		assertEquals(message, expected.getTopDownValue(), actual.getTopDownValue(), 0.09);
		assertEquals(message, expected.hasDeclared(), actual.hasDeclared());
		assertEquals(message, expected.getDeclared(), actual.getDeclared());
	}

	public static void assertEquality(final Effort original, final Effort other) {
		assertEquality("Checking equality of effort.", original, other);
	}

	public static void assertNotEquals(final Object expected, final Object actual) {
		// TODO Invert the order of arguments of other methods of this class.
		Assert.assertFalse(expected.equals(actual));
	}
}
