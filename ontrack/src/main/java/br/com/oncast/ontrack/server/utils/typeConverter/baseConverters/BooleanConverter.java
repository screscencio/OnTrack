package br.com.oncast.ontrack.server.utils.typeConverter.baseConverters;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

public class BooleanConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof Boolean)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not a Boolean.");
		return new Boolean((Boolean) originalBean);
	}

}
