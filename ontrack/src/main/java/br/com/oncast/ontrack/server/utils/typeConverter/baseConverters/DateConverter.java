package br.com.oncast.ontrack.server.utils.typeConverter.baseConverters;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

import java.util.Date;

public class DateConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(Date.class.isAssignableFrom(originalBean.getClass()))) throw new TypeConverterException("Cannot convert " + originalBean.getClass()
				+ ": it is not a Date.");
		return new Date(((Date) originalBean).getTime());
	}

}
