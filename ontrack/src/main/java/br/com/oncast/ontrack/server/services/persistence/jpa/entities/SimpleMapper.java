package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;

public class SimpleMapper extends TypeMapper {

	public SimpleMapper(final TypeMapper myTrailer) {
		super(myTrailer);
	}

	@Override
	protected Object mapField(final Object from, final Object to, final Field field) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		return field.get(from);
	}

	@Override
	protected boolean isMyAction(final Field field) {
		return true;
	}

}
