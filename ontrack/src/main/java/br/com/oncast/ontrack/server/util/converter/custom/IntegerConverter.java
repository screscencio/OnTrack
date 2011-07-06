package br.com.oncast.ontrack.server.util.converter.custom;

import br.com.oncast.ontrack.server.util.converter.TypeConverter;
import br.com.oncast.ontrack.shared.exceptions.converter.TypeConverterException;

public class IntegerConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof Integer)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not an Integer.");
		return new Integer((Integer) originalBean);
	}

}
