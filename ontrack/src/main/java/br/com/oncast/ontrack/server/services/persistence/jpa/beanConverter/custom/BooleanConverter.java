package br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.custom;

import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.TypeConverter;
import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.exceptions.BeanConverterException;

public class BooleanConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws BeanConverterException {
		if (!(originalBean instanceof Boolean)) throw new BeanConverterException("Cannot convert " + originalBean.getClass() + ": it is not a Boolean.");
		return new Boolean((Boolean) originalBean);
	}

}
