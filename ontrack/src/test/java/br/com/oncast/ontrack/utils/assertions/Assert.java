package br.com.oncast.ontrack.utils.assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Assert {

	public static void assertDeepEquals(final Scope actual, final Scope expected) {
		for (int i = 0; i < actual.getChildren().size(); i++) {
			assertTrue("Checking equality of scope '" + actual.getDescription() + "'.",
					actual.getChild(i).getDescription().equals(expected.getChild(i).getDescription()));
			assertEquality("Checking equality of effort of scope '" + actual.getChild(i).getDescription() + "'.", actual.getChild(i).getEffort(), expected
					.getChild(i).getEffort());

			assertDeepEquals(actual.getChild(i), expected.getChild(i));
		}
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
}
