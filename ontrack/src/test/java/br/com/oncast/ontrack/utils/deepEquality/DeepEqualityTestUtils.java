package br.com.oncast.ontrack.utils.deepEquality;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.server.utils.introspector.IntrospectionEngine;
import br.com.oncast.ontrack.server.utils.introspector.IntrospectionException;
import br.com.oncast.ontrack.server.utils.introspector.Introspector;
import br.com.oncast.ontrack.utils.deepEquality.custom.DeepEqualityComparator;
import br.com.oncast.ontrack.utils.deepEquality.custom.ScopeTreeDeepEqualityComparator;
import br.com.oncast.ontrack.utils.identedLogger.ErrorOnlyIdentedLogger;
import br.com.oncast.ontrack.utils.identedLogger.IdentedLogger;

// TODO Implement a logger that informs where the error occurred.
public class DeepEqualityTestUtils {

	private static double requiredFloatingPointPrecision = 0.01;
	private static Map<Class<?>, DeepEqualityComparator<?>> customComparatorMap = new HashMap<Class<?>, DeepEqualityComparator<?>>();
	private static IdentedLogger LOGGER = new ErrorOnlyIdentedLogger();

	static {
		// TODO +Externalize this so that specific application comparators are registered by the application itself.
		setCustomDeepEqualityComparator(ScopeTree.class, new ScopeTreeDeepEqualityComparator());
	}

	public static <T> void setCustomDeepEqualityComparator(final Class<T> targetClass, final DeepEqualityComparator<T> deepEqualityComparator) {
		customComparatorMap.put(targetClass, deepEqualityComparator);
	}

	public static <T> void removeCustomDeepEqualityComparator(final Class<T> targetClass) {
		if (!customComparatorMap.containsKey(targetClass)) throw new DeepEqualityException(
				"There was not possible to remove the custom comparator for the class " + targetClass
						+ ". There is no custom comparator registered for this class");

		customComparatorMap.remove(targetClass);
	}

	public static double getRequiredFloatingPointPrecision() {
		return requiredFloatingPointPrecision;
	}

	public static void setRequiredFloatingPointPrecision(final double requiredFloatingPointPrecision) {
		DeepEqualityTestUtils.requiredFloatingPointPrecision = requiredFloatingPointPrecision;
	}

	public static void assertObjectEquality(final Object expected, final Object actual) throws DeepEqualityException {
		if (expected == null) {
			Assert.assertNull("Expected class is null but actual is not.\n" + LOGGER.getCurrentLogHierarchy(), actual);
			return;
		}
		LOGGER.log("Asserting object equality in " + expected.getClass().getSimpleName());
		LOGGER.indent();
		try {
			if (expected == actual) {
				LOGGER.log("Equality asserted: Both represent the same instance.");
				return;
			}

			Assert.assertNotNull("Given expected object " + expected.getClass().getName() + " is null.\n" + LOGGER.getCurrentLogHierarchy(), expected);
			Assert.assertNotNull("Given actual object " + actual.getClass().getName() + " is null.\n" + LOGGER.getCurrentLogHierarchy(), actual);
			Assert.assertTrue("Incompatible classes. Expected class '" + expected.getClass().getName() + "' cannot be assignable from '"
					+ actual.getClass().getName() + "'.\n" + LOGGER.getCurrentLogHierarchy(), expected.getClass().isAssignableFrom(actual.getClass()));

			if (hasCustomDeepEqualityComparator(expected.getClass())) assertCustomDeepEquality(expected, actual);
			else if (expected instanceof Character) assertSimpleEquality(expected, actual);
			else if (expected instanceof Byte) assertSimpleEquality(expected, actual);
			else if (expected instanceof Short) assertSimpleEquality(expected, actual);
			else if (expected instanceof Integer) assertSimpleEquality(expected, actual);
			else if (expected instanceof Long) assertSimpleEquality(expected, actual);
			else if (expected instanceof Float) assertFloatingPointEquality((Float) expected, (Float) actual);
			else if (expected instanceof Double) assertFloatingPointEquality((Double) expected, (Double) actual);
			else if (expected instanceof Boolean) assertSimpleEquality(expected, actual);
			else if (expected instanceof String) assertSimpleEquality(expected, actual);
			else if (expected instanceof Collection<?>) assertCollectionEquality((Collection<?>) expected, (Collection<?>) actual);
			else if (expected instanceof Map<?, ?>) assertMapEquality((Map<?, ?>) expected, (Map<?, ?>) actual);
			else assertComplexObjectEquality(expected, actual);
		}
		finally {
			LOGGER.outdent();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void assertCustomDeepEquality(final Object expected, final Object actual) {
		((DeepEqualityComparator) customComparatorMap.get(expected.getClass())).assertObjectEquality(expected, actual);
	}

	private static void assertMapEquality(final Map<?, ?> expected, final Map<?, ?> actual) throws DeepEqualityException {
		LOGGER.log("Asserting map");
		Assert.assertEquals("Different map keySet.\n" + LOGGER.getCurrentLogHierarchy(), expected.keySet(), actual.keySet());
		for (final Object key : (Set<?>) expected.keySet()) {
			LOGGER.indent();
			LOGGER.log("Asserting map item '" + key.toString() + "'");
			assertObjectEquality(expected.get(key), actual.get(key));
			LOGGER.outdent();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void assertCollectionEquality(final Collection<?> expected, final Collection<?> actual) throws DeepEqualityException {
		LOGGER.log("Asserting collection");
		Assert.assertEquals("Different collection sizes.\n" + LOGGER.getCurrentLogHierarchy(), expected.size(), actual.size());
		final List<?> expectedList = new ArrayList(expected);
		final List<?> actualList = new ArrayList(actual);
		for (int i = 0; i < expectedList.size(); i++) {
			LOGGER.indent();
			LOGGER.log("Asserting collection item '" + i + "'");
			assertObjectEquality(expectedList.get(i), actualList.get(i));
			LOGGER.outdent();
		}
	}

	protected static void assertFloatingPointEquality(final Float expected, final Float actual) {
		Assert.assertEquals(LOGGER.getCurrentLogHierarchy(), expected, actual, requiredFloatingPointPrecision);
	}

	protected static void assertFloatingPointEquality(final Double expected, final Double actual) {
		Assert.assertEquals(LOGGER.getCurrentLogHierarchy(), expected, actual, requiredFloatingPointPrecision);
	}

	protected static void assertSimpleEquality(final Object expected, final Object actual) {
		Assert.assertEquals(LOGGER.getCurrentLogHierarchy(), expected, actual);
	}

	private static void assertComplexObjectEquality(final Object expected, final Object actual) throws DeepEqualityException {
		try {
			IntrospectionEngine.introspectThroughDeclaredFields(expected, new Introspector<Field>() {

				@Override
				public void introspect(final Field field) throws Exception {
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
						LOGGER.log("Ignoring field: '" + field.getName() + "'");
						return;
					}
					if (field.isAnnotationPresent(IgnoredByDeepEquality.class)) {
						LOGGER.log("Ignoring field: '" + field.getName() + "'");
						return;
					}
					if (field.isAnnotationPresent(DeepEqualityByGetter.class)) {
						LOGGER.log("Using getter method to assert: '" + field.getName() + "'");
						assertUsingGetter(expected, actual, field);
						return;
					}
					LOGGER.log("Asserting '" + field.getName() + "'");
					LOGGER.indent();

					final Object expectedValue = IntrospectionEngine.getFieldValue(expected, field);
					final Object actualValue = IntrospectionEngine.getFieldValue(actual, field);

					Assert.assertSame("Incompatible classes.\n" + LOGGER.getCurrentLogHierarchy(), expected.getClass(), actual.getClass());
					assertObjectEquality(expectedValue, actualValue);
					LOGGER.outdent();
				}
			});
		}
		catch (final IntrospectionException e) {
			throw new DeepEqualityException("A problem while checking the 'deep equality' was found.", e);
		}
	}

	private static void assertUsingGetter(final Object expected, final Object actual, final Field field) throws IntrospectionException {
		final String methodName = IntrospectionEngine.getEquivalentGetterMethodName(field);

		LOGGER.log("Asserting method '" + methodName + "'");
		LOGGER.indent();

		final Object expectedValue = IntrospectionEngine.getMethodValue(expected, methodName);
		final Object actualValue = IntrospectionEngine.getMethodValue(actual, methodName);

		Assert.assertSame("Incompatible classes.\n" + LOGGER.getCurrentLogHierarchy(), expected.getClass(), actual.getClass());
		assertObjectEquality(expectedValue, actualValue);
		LOGGER.outdent();
	}

	private static boolean hasCustomDeepEqualityComparator(final Class<? extends Object> clazz) {
		return customComparatorMap.containsKey(clazz);
	}
}
