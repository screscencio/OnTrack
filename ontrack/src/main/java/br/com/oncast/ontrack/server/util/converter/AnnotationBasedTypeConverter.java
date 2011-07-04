package br.com.oncast.ontrack.server.util.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import br.com.oncast.ontrack.server.util.converter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertUsing;
import br.com.oncast.ontrack.shared.exceptions.converter.BeanConverterException;

class AnnotationBasedTypeConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws BeanConverterException {
		final Object destinationBean = createDestinationInstance(originalBean);
		mapFields(originalBean, destinationBean);

		return destinationBean;
	}

	private Object createDestinationInstance(final Object sourceBean) throws BeanConverterException {
		final Class<?> sourceBeanClass = sourceBean.getClass();

		final ConvertTo annotation = sourceBeanClass.getAnnotation(ConvertTo.class);
		if (annotation == null) throw new BeanConverterException("The source class " + sourceBeanClass.getSimpleName() + " must be annotated with "
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
			throw new BeanConverterException("The mapping's destination class could not be accessed.", e);
		}
		catch (final ClassCastException e) {
			throw new BeanConverterException("The mapping's destination class cannot be instantiated to the specified generic type.", e);
		}
		catch (final SecurityException e) {
			throw new BeanConverterException("The mapping's destination class could not be instantiated because of security reasons.", e);
		}
		catch (final NoSuchMethodException e) {
			throw new BeanConverterException(
					"The mapping's destination class could not be instantiated because there is no default constructor (may be protected).", e);
		}
		catch (final Exception e) {
			throw new BeanConverterException("The mapping's destination class could not be instantiated.", e);
		}

		return destinationBean;
	}

	private void mapFields(final Object sourceInstance, final Object destinationInstance) throws BeanConverterException {
		final Field[] sourceFields = sourceInstance.getClass().getDeclaredFields();
		for (final Field sourceField : sourceFields) {
			final Field destinationField = findDestinationField(destinationInstance, sourceField);
			mapField(sourceInstance, sourceField, destinationInstance, destinationField);
		}
	}

	private Field findDestinationField(final Object destination, final Field sourceField) throws BeanConverterException {
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
			throw new BeanConverterException("It was not possible to access the mapping's destination field.", e);
		}
		if (field == null) throw new BeanConverterException("It was not possible to locate the mapping's destination field.");

		return field;
	}

	private void mapField(final Object sourceInstance, final Field sourceField, final Object destinationInstance, final Field destinationField)
			throws BeanConverterException {

		final boolean sourceFieldAccessibility = sourceField.isAccessible();
		if (!sourceFieldAccessibility) sourceField.setAccessible(true);

		final boolean destinationFieldAccessibility = destinationField.isAccessible();
		if (!destinationFieldAccessibility) destinationField.setAccessible(true);

		final Object sourceFieldValue = getFieldValue(sourceInstance, sourceField);
		final Object destinationFieldValue = convertValue(sourceField, sourceFieldValue);
		setFieldValue(destinationInstance, destinationField, destinationFieldValue);

		sourceField.setAccessible(sourceFieldAccessibility);
		destinationField.setAccessible(destinationFieldAccessibility);
	}

	private Object convertValue(final Field sourceField, final Object sourceFieldValue) throws BeanConverterException {
		final TypeConverter converter = (sourceField.isAnnotationPresent(ConvertUsing.class)) ? instantiateConverter(sourceField.getAnnotation(
				ConvertUsing.class).value()) : new GeneralTypeConverter();

		return converter.convert(sourceFieldValue);
	}

	private TypeConverter instantiateConverter(final Class<? extends TypeConverter> converterClass) throws BeanConverterException {
		TypeConverter instance;
		try {
			instance = converterClass.newInstance();
		}
		catch (final Exception e) {
			throw new BeanConverterException("It was not possible to instantiate the converter " + converterClass.getName() + ".");
		}
		return instance;
	}

	private String getFieldRepresentationName(final Field field) {
		return (field.isAnnotationPresent(ConversionAlias.class)) ? field.getAnnotation(ConversionAlias.class).value() : field.getName();
	}

	private Object getFieldValue(final Object instance, final Field field) throws BeanConverterException {
		Object fieldValue;
		try {
			fieldValue = field.get(instance);
		}
		catch (final IllegalArgumentException e) {
			throw new BeanConverterException("Internal error while accessing the " + instance.getClass().getSimpleName() + "'s field " + field.getName()
					+ " while trying to 'get' its value.", e);
		}
		catch (final IllegalAccessException e) {
			throw new BeanConverterException("The " + instance.getClass().getSimpleName() + "'s field " + field.getName()
					+ " could not be accessed while trying to 'get' its value.", e);
		}
		return fieldValue;
	}

	private void setFieldValue(final Object instance, final Field field, final Object value) throws BeanConverterException {
		try {
			field.set(instance, value);
		}
		catch (final IllegalArgumentException e) {
			throw new BeanConverterException("Internal error while accessing the " + instance.getClass().getSimpleName() + "'s field " + field.getName()
					+ " while trying to 'set' its value.", e);
		}
		catch (final IllegalAccessException e) {
			throw new BeanConverterException("The " + instance.getClass().getSimpleName() + "'s field " + field.getName()
					+ " could not be accessed while trying to 'set' its value.", e);
		}
	}
}
