package br.com.oncast.ontrack.server.util.converter;

import br.com.oncast.ontrack.server.util.converter.exceptions.BeanConverterException;

public interface TypeConverter {
	Object convert(Object originalBean) throws BeanConverterException;
}
