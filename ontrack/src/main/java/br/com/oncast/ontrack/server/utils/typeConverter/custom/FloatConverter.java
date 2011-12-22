package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

public class FloatConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof Float)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not an Integer.");
		return new Float((Float) originalBean);
	}

}
