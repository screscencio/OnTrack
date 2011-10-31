package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import java.util.Date;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

/**
 * Converts {@link Date} instances. This converter discards the received object value and sets a new {@link Date} instance generated at the time of conversion.
 */
public class ServerTimeConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof Date)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not a Date.");
		return new Date();
	}

}
