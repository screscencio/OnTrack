package br.com.oncast.ontrack.server.services.persistence.jpa.mapping;

import java.lang.reflect.Field;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.annotations.MapTo;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.exceptions.BeanMapperException;

class GenericTypeMapper implements BeanTypeMapper {

	@Override
	public Object createMappedBean(final Object originalBean) throws BeanMapperException {
		final Object destinationBean = createDestinationInstance(originalBean);
		mapFields(originalBean, destinationBean);

		return destinationBean;
	}

	private Object createDestinationInstance(final Object sourceBean) throws BeanMapperException {
		final Class<?> sourceBeanClass = sourceBean.getClass();

		// FIXME
		final MapTo annotation = sourceBeanClass.getAnnotation(MapTo.class);
		if (annotation == null) throw new BeanMapperException("The source class " + sourceBeanClass.getSimpleName() + " must be annotated with " + MapTo.class);

		Object destinationBean;
		try {
			destinationBean = annotation.value().newInstance();
		}
		catch (final InstantiationException e) {
			throw new BeanMapperException("The mapping's destination class could not be instantiated.", e);
		}
		catch (final IllegalAccessException e) {
			throw new BeanMapperException("The mapping's destination class could not be accessed.", e);
		}
		catch (final ClassCastException e) {
			throw new BeanMapperException("The mapping's destination class cannot be instantiated to the specified generic type.", e);
		}

		return destinationBean;
	}

	private void mapFields(final Object sourceInstance, final Object destinationInstance) throws BeanMapperException {
		final Field[] sourceFields = sourceInstance.getClass().getDeclaredFields();
		for (final Field sourceField : sourceFields) {
			final Field destinationField = findDestinationField(destinationInstance, sourceField);
			mapField(sourceInstance, sourceField, destinationInstance, destinationField);
		}
	}

	private void mapField(final Object sourceInstance, final Field sourceField, final Object destinationInstance, final Field destinationField)
			throws BeanMapperException {
		final boolean sourceFieldAccessibility = sourceField.isAccessible();
		if (!sourceFieldAccessibility) sourceField.setAccessible(true);

		final boolean destinationFieldAccessibility = destinationField.isAccessible();
		if (!destinationFieldAccessibility) destinationField.setAccessible(true);

		final Object sourceFieldValue = getFieldValue(sourceInstance, sourceField);
		final Object destinationFieldValue = new BeanMapper().createMappedBean(sourceFieldValue);
		setFieldValue(destinationInstance, destinationField, destinationFieldValue);

		sourceField.setAccessible(sourceFieldAccessibility);
		destinationField.setAccessible(destinationFieldAccessibility);
	}

	private Field findDestinationField(final Object destination, final Field sourceField) throws BeanMapperException {
		Field field;
		try {
			// FIXME Search for annotations in the fields.
			field = destination.getClass().getDeclaredField(sourceField.getName());
		}
		catch (final SecurityException e) {
			throw new BeanMapperException("It was not possible to access the mapping's destination field.", e);
		}
		catch (final NoSuchFieldException e) {
			throw new BeanMapperException("It was not possible to locate the mapping's destination field.", e);
		}
		return field;
	}

	private Object getFieldValue(final Object instance, final Field field) throws BeanMapperException {
		Object fieldValue;
		try {
			fieldValue = field.get(instance);
		}
		catch (final IllegalArgumentException e) {
			throw new BeanMapperException("Internal error while accessing the " + instance.getClass().getSimpleName() + "'s field " + field.getName()
					+ " while trying to 'get' its value.", e);
		}
		catch (final IllegalAccessException e) {
			throw new BeanMapperException("The " + instance.getClass().getSimpleName() + "'s field " + field.getName()
					+ " could not be accessed while trying to 'get' its value.", e);
		}
		return fieldValue;
	}

	private void setFieldValue(final Object instance, final Field field, final Object value) throws BeanMapperException {
		try {
			field.set(instance, value);
		}
		catch (final IllegalArgumentException e) {
			throw new BeanMapperException("Internal error while accessing the " + instance.getClass().getSimpleName() + "'s field " + field.getName()
					+ " while trying to 'set' its value.", e);
		}
		catch (final IllegalAccessException e) {
			throw new BeanMapperException("The " + instance.getClass().getSimpleName() + "'s field " + field.getName()
					+ " could not be accessed while trying to 'set' its value.", e);
		}
	}
}
