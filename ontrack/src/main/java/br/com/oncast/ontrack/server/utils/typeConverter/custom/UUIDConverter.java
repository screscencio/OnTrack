package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UUIDConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof UUID)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not an UUID.");
		return ((UUID) originalBean).toStringRepresentation();
	}

}
