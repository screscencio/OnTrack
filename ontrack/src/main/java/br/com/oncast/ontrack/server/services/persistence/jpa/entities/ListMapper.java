package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListMapper {

	void map(final Object action, final ModelActionEntity actionEntity, final Field field) throws IllegalAccessException, NoSuchFieldException {
		final Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			final ParameterizedType pt = (ParameterizedType) type;

			final Type[] typeArguments = pt.getActualTypeArguments();
			if (typeArguments.length > 1) throw new RuntimeException("The mapping of beans doesn't support more than one generic type at the same property.");

			final List childList = new ArrayList((List) field.get(action));
			final List childEntityList = new ArrayList();
			for (final Object childAction : childList) {
				childEntityList.add(BeanMapper.map(childAction));
			}
			// FIXME Remove this redundant code
			final Field entityField = actionEntity.getClass().getDeclaredField(field.getName());
			entityField.setAccessible(true);
			entityField.set(actionEntity, childEntityList);
		}
	}
}
