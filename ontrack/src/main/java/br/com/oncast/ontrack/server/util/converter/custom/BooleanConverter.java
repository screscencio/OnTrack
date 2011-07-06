package br.com.oncast.ontrack.server.util.converter.custom;

import br.com.oncast.ontrack.server.util.converter.TypeConverter;
import br.com.oncast.ontrack.shared.exceptions.converter.TypeConverterException;

public class BooleanConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof Boolean)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not a Boolean.");
		return new Boolean((Boolean) originalBean);
	}

}
