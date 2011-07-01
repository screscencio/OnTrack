package br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter;

import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.exceptions.BeanConverterException;

public interface TypeConverter {
	Object convert(Object originalBean) throws BeanConverterException;
}
