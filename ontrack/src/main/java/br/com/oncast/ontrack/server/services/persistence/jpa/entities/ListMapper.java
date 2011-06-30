package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListMapper extends TypeMapper {

	public ListMapper(final TypeMapper myTrailer) {
		super(myTrailer);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object mapField(final Object action, final Object actionEntity, final Field field) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		final Type type = field.getGenericType();
		final ParameterizedType pt = (ParameterizedType) type;

		final Type[] typeArguments = pt.getActualTypeArguments();
		if (typeArguments.length > 1) throw new RuntimeException("The mapping of beans doesn't support more than one generic type at the same property.");

		final List childList = new ArrayList((List) field.get(action));
		final List childEntityList = new ArrayList();
		for (final Object childAction : childList) {
			childEntityList.add(BeanMapper.map(childAction));
		}
		return childEntityList;
	}

	@Override
	protected boolean isMyAction(final Field field) {
		return field.getType().equals(List.class);
	}

}
