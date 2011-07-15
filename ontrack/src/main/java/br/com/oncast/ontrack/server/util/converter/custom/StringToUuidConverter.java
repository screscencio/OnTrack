package br.com.oncast.ontrack.server.util.converter.custom;

import br.com.oncast.ontrack.server.util.converter.TypeConverter;
import br.com.oncast.ontrack.server.util.converter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class StringToUuidConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof String)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not a String.");
		return new UUID((String) originalBean);
	}
}
