package br.com.oncast.ontrack.server.util.converter.custom;

import br.com.oncast.ontrack.server.util.converter.TypeConverter;
import br.com.oncast.ontrack.shared.exceptions.converter.BeanConverterException;

public class BooleanConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws BeanConverterException {
		if (!(originalBean instanceof Boolean)) throw new BeanConverterException("Cannot convert " + originalBean.getClass() + ": it is not a Boolean.");
		return new Boolean((Boolean) originalBean);
	}

}
