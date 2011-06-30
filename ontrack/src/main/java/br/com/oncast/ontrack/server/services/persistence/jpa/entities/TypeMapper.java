package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;

public abstract class TypeMapper {

	private final TypeMapper myTrailer;

	public TypeMapper(final TypeMapper myTrailer) {
		this.myTrailer = myTrailer;
	}

	public final Object map(final Object from, final Object to, final Field field) {
		if (isMyAction(field)) {
			try {
				return mapField(from, to, field);
			}
			catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (myTrailer != null) return myTrailer.map(from, to, field);

		throw new RuntimeException("Mapping type not supported.");
	}

	protected abstract Object mapField(final Object from, Object to, Field field) throws IllegalArgumentException, IllegalAccessException, SecurityException,
			NoSuchFieldException;

	protected abstract boolean isMyAction(Field field);

}
