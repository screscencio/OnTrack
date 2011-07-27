package br.com.oncast.ontrack.server.util.typeConverter.custom;

import br.com.oncast.ontrack.server.util.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.util.typeConverter.exceptions.TypeConverterException;

public class StringConverter implements TypeConverter {
	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof String)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not a String.");
		return new String((String) originalBean);
	}
}
