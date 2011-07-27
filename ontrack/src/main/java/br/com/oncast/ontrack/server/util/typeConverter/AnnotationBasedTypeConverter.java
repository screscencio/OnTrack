package br.com.oncast.ontrack.server.util.typeConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import br.com.oncast.ontrack.server.util.introspector.IntrospectionEngine;
import br.com.oncast.ontrack.server.util.introspector.IntrospectionException;
import br.com.oncast.ontrack.server.util.introspector.Introspector;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.util.typeConverter.exceptions.TypeConverterException;

class AnnotationBasedTypeConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		final Object destinationBean = createDestinationInstance(originalBean);
		mapFields(originalBean, destinationBean);

		return destinationBean;
	}

	private Object createDestinationInstance(final Object sourceBean) throws TypeConverterException {
		final Class<?> sourceBeanClass = sourceBean.getClass();

		final ConvertTo annotation = sourceBeanClass.getAnnotation(ConvertTo.class);
		if (annotation == null) throw new TypeConverterException("The source class " + sourceBeanClass.getSimpleName() + " must be annotated with "
				+ ConvertTo.class);

		Object destinationBean;
		try {
			final Class<?> clazz = annotation.value();
			final Constructor<?> constructor = clazz.getDeclaredConstructor();
			final boolean constructorAccessibility = constructor.isAccessible();
			constructor.setAccessible(true);
			destinationBean = constructor.newInstance();
			constructor.setAccessible(constructorAccessibility);
		}
		catch (final IllegalAccessException e) {
			throw new TypeConverterException("The mapping's destination class could not be accessed.", e);
		}
		catch (final ClassCastException e) {
			throw new TypeConverterException("The mapping's destination class cannot be instantiated to the specified generic type.", e);
		}
		catch (final SecurityException e) {
			throw new TypeConverterException("The mapping's destination class could not be instantiated because of security reasons.", e);
		}
		catch (final NoSuchMethodException e) {
			throw new TypeConverterException(
					"The mapping's destination class could not be instantiated because there is no default constructor (may be protected).", e);
		}
		catch (final Exception e) {
			throw new TypeConverterException("The mapping's destination class could not be instantiated.", e);
		}

		return destinationBean;
	}

	private void mapFields(final Object sourceInstance, final Object destinationInstance) throws TypeConverterException {
		try {
			IntrospectionEngine.introspectThroughDeclaredFields(sourceInstance, new Introspector<Field>() {

				@Override
				public void introspect(final Field sourceField) throws Exception {
					final Field destinationField = findDestinationField(destinationInstance, sourceField);
					mapField(sourceInstance, sourceField, destinationInstance, destinationField);
				}
			});
		}
		catch (final IntrospectionException e) {
			throw new TypeConverterException("It was not possible to map fields from '" + sourceInstance.getClass().getName() + "' to '"
					+ destinationInstance.getClass().getName() + "'.", e);
		}
	}

	private Field findDestinationField(final Object destination, final Field sourceField) throws TypeConverterException {
		Field field = null;
		try {
			final String sourceFieldRepresentationName = getFieldRepresentationName(sourceField);

			final Field[] destinationFields = destination.getClass().getDeclaredFields();
			for (final Field destinationField : destinationFields) {
				final String destinationFieldRepresentationName = getFieldRepresentationName(destinationField);
				if (!sourceFieldRepresentationName.equals(destinationFieldRepresentationName)) continue;

				field = destinationField;
				break;
			}
		}
		catch (final SecurityException e) {
			throw new TypeConverterException("It was not possible to access the mapping's destination field.", e);
		}
		if (field == null) throw new TypeConverterException("It was not possible to locate the mapping's destination field.");

		return field;
	}

	private void mapField(final Object sourceInstance, final Field sourceField, final Object destinationInstance, final Field destinationField)
			throws TypeConverterException, IntrospectionException {
		final Object sourceFieldValue = IntrospectionEngine.getFieldValue(sourceInstance, sourceField);
		final Object destinationFieldValue = convertValue(sourceField, sourceFieldValue);
		IntrospectionEngine.setFieldValue(destinationInstance, destinationField, destinationFieldValue);
	}

	private Object convertValue(final Field sourceField, final Object sourceFieldValue) throws TypeConverterException {
		if (sourceFieldValue == null) return null;

		final TypeConverter converter = (sourceField.isAnnotationPresent(ConvertUsing.class)) ? instantiateConverter(sourceField.getAnnotation(
				ConvertUsing.class).value()) : new GeneralTypeConverter();

		return converter.convert(sourceFieldValue);
	}

	private TypeConverter instantiateConverter(final Class<? extends TypeConverter> converterClass) throws TypeConverterException {
		TypeConverter instance;
		try {
			instance = converterClass.newInstance();
		}
		catch (final Exception e) {
			throw new TypeConverterException("It was not possible to instantiate the converter " + converterClass.getName() + ".");
		}
		return instance;
	}

	private String getFieldRepresentationName(final Field field) {
		return (field.isAnnotationPresent(ConversionAlias.class)) ? field.getAnnotation(ConversionAlias.class).value() : field.getName();
	}
}
