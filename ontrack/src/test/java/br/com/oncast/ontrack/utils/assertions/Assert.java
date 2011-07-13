package br.com.oncast.ontrack.utils.assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Assert {

	public static void assertDeepEquals(final Scope original, final Scope other) {
		for (int i = 0; i < original.getChildren().size(); i++) {
			assertTrue("Checking equality of scope with description '" + original.getDescription() + "'",
					original.getChild(i).getDescription().equals(other.getChild(i).getDescription()));
			assertEquality("Checking equality of effort of scope '" + original.getDescription(), original.getChild(i).getEffort(), other.getChild(i)
					.getEffort());

			assertDeepEquals(original.getChild(i), other.getChild(i));
		}
	}

	public static void assertEquality(final String message, final Effort original, final Effort other) {
		assertEquals(message, original.getBottomUpValue(), other.getBottomUpValue(), 0.09);
		assertEquals(message, original.getTopDownValue(), other.getTopDownValue(), 0.09);
		assertEquals(message, original.hasDeclared(), other.hasDeclared());
		assertEquals(message, original.getDeclared(), other.getDeclared());
	}
}
