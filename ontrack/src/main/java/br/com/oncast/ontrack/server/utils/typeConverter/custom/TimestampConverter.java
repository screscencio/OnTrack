package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import java.sql.Timestamp;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

public class TimestampConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof Timestamp)) throw new TypeConverterException("Cannot convert " + originalBean.getClass()
				+ ": it is not a Timestamp.");
		return new Timestamp(((Timestamp) originalBean).getTime());
	}

}
