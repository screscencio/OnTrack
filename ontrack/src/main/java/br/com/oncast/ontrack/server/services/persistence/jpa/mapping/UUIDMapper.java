package br.com.oncast.ontrack.server.services.persistence.jpa.mapping;

import java.lang.reflect.Field;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UUIDMapper extends TypeMapper {

	public UUIDMapper(final TypeMapper myTrailer) {
		super(myTrailer);
	}

	@Override
	protected Object mapField(final Object from, final Object to, final Field field) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		return field.get(from).toString();
	}

	@Override
	protected boolean isMyAction(final Field field) {
		return field.getType().equals(UUID.class);
	}

}
