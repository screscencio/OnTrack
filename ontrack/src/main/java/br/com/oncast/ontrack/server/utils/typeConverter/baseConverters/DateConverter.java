package br.com.oncast.ontrack.server.utils.typeConverter.baseConverters;

import java.util.Date;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

public class DateConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(Date.class.isAssignableFrom(originalBean.getClass()))) throw new TypeConverterException("Cannot convert " + originalBean.getClass()
				+ ": it is not a Date.");
		return new Date(((Date) originalBean).getTime());
	}

}
