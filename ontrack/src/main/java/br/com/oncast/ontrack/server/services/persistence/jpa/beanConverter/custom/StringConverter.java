package br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.custom;

import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.TypeConverter;
import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.exceptions.BeanConverterException;

public class StringConverter implements TypeConverter {
	@Override
	public Object convert(final Object originalBean) throws BeanConverterException {
		if (!(originalBean instanceof String)) throw new BeanConverterException("Cannot convert " + originalBean.getClass() + ": it is not a String.");
		return new String((String) originalBean);
	}
}
