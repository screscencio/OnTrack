package br.com.oncast.ontrack.server.utils.typeConverter.baseConverters;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

public class EnumConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!originalBean.getClass().isEnum()) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not an Enum.");
		return originalBean;
	}

}
