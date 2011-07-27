package br.com.oncast.ontrack.utils.deepEquality;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.server.util.introspector.IntrospectionEngine;
import br.com.oncast.ontrack.server.util.introspector.IntrospectionException;
import br.com.oncast.ontrack.server.util.introspector.Introspector;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.utils.deepEquality.custom.DeepEqualityComparator;
import br.com.oncast.ontrack.utils.deepEquality.custom.ScopeTreeDeepEqualityComparator;

// TODO Implement a logger that informs where the error occurred.
public class DeepEqualityTestUtils {

	private static double requiredFloatingPointPrecision = 0.01;
	private static Map<Class<?>, DeepEqualityComparator<?>> customComparatorMap = new HashMap<Class<?>, DeepEqualityComparator<?>>();

	static {
		// TODO +Externalize this so that specific application comparators are registered by the application itself.
		setCustomDeepEqualityComparator(ScopeTree.class, new ScopeTreeDeepEqualityComparator());
	}

	public static void assertObjectEquality(final Object expected, final Object actual) throws DeepEqualityException {
		if (expected == actual) return;

		Assert.assertNotNull("Given expected object " + expected.getClass().getName() + " is null.", expected);
		Assert.assertNotNull("Given actual object " + actual.getClass().getName() + " is null.", actual);
		Assert.assertTrue("Incompatible classes. Expected class '" + expected.getClass().getName() + "' cannot be assignable from '"
				+ actual.getClass().getName() + "'.", expected.getClass().isAssignableFrom(actual.getClass()));

		if (expected instanceof Character) assertSimpleEquality(expected, actual);
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
		else if (hasCustomDeepEqualityComparator(expected.getClass())) assertCustomDeepEquality(expected, actual);
		else assertComplexObjectEquality(expected, actual);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void assertCustomDeepEquality(final Object expected, final Object actual) {
		((DeepEqualityComparator) customComparatorMap.get(expected.getClass())).assertObjectEquality(expected, actual);
	}

	private static boolean hasCustomDeepEqualityComparator(final Class<? extends Object> clazz) {
		return customComparatorMap.containsKey(clazz);
	}

	public static <T> void setCustomDeepEqualityComparator(final Class<T> targetClass, final DeepEqualityComparator<T> deepEqualityComparator) {
		customComparatorMap.put(targetClass, deepEqualityComparator);
	}

	public static void removeCustomDeepEqualityComparator(final Class<Effort> targetClass) {
		if (!customComparatorMap.containsKey(targetClass)) throw new DeepEqualityException(
				"There was not possible to remove the custom comparator for the class " + targetClass
						+ ". There is no custom comparator registered for this class");

		customComparatorMap.remove(targetClass);
	}

	private static void assertMapEquality(final Map<?, ?> expected, final Map<?, ?> actual) throws DeepEqualityException {
		Assert.assertEquals(expected.keySet(), actual.keySet());
		for (final Object key : (Set<?>) expected.keySet())
			assertObjectEquality(expected.get(key), actual.get(key));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void assertCollectionEquality(final Collection<?> expected, final Collection<?> actual) throws DeepEqualityException {
		Assert.assertEquals(expected.size(), actual.size());
		final List<?> expectedList = new ArrayList(expected);
		final List<?> actualList = new ArrayList(actual);
		for (int i = 0; i < expectedList.size(); i++)
			assertObjectEquality(expectedList.get(i), actualList.get(i));
	}

	protected static void assertFloatingPointEquality(final Float expected, final Float actual) {
		Assert.assertEquals(expected, actual, requiredFloatingPointPrecision);
	}

	protected static void assertFloatingPointEquality(final Double expected, final Double actual) {
		Assert.assertEquals(expected, actual, requiredFloatingPointPrecision);
	}

	protected static void assertSimpleEquality(final Object expected, final Object actual) {
		Assert.assertEquals(expected, actual);
	}

	private static void assertComplexObjectEquality(final Object expected, final Object actual) throws DeepEqualityException {
		try {
			IntrospectionEngine.introspectThroughDeclaredFields(expected, new Introspector<Field>() {

				@Override
				public void introspect(final Field field) throws Exception {
					if (field.isAnnotationPresent(IgnoredByDeepEquality.class)) return;

					final Object expectedValue = IntrospectionEngine.getFieldValue(expected, field);
					final Object actualValue = IntrospectionEngine.getFieldValue(actual, field);

					Assert.assertSame("Incompatible classes.", expected.getClass(), actual.getClass());
					assertObjectEquality(expectedValue, actualValue);
				}
			});
		}
		catch (final IntrospectionException e) {
			throw new DeepEqualityException("A problem while checking the 'deep equality' was found.", e);
		}
	}

	public static double getRequiredFloatingPointPrecision() {
		return requiredFloatingPointPrecision;
	}

	public static void setRequiredFloatingPointPrecision(final double requiredFloatingPointPrecision) {
		DeepEqualityTestUtils.requiredFloatingPointPrecision = requiredFloatingPointPrecision;
	}

}
