package br.com.oncast.ontrack.server.util.converter.custom;

import br.com.oncast.ontrack.server.util.converter.TypeConverter;
import br.com.oncast.ontrack.server.util.converter.exceptions.BeanConverterException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UUIDConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws BeanConverterException {
		if (!(originalBean instanceof UUID)) throw new BeanConverterException("Cannot convert " + originalBean.getClass() + ": it is not an UUID.");
		return ((UUID) originalBean).toStringRepresentation();
	}

}
