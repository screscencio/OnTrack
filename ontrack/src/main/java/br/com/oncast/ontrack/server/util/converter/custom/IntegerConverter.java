package br.com.oncast.ontrack.server.util.converter.custom;

import br.com.oncast.ontrack.server.util.converter.TypeConverter;
import br.com.oncast.ontrack.server.util.converter.exceptions.BeanConverterException;

public class IntegerConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws BeanConverterException {
		if (!(originalBean instanceof Integer)) throw new BeanConverterException("Cannot convert " + originalBean.getClass() + ": it is not an Integer.");
		return new Integer((Integer) originalBean);
	}

}
