package br.com.oncast.ontrack.server.utils.typeConverter;

import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

public interface TypeConverter {
	Object convert(Object originalBean) throws TypeConverterException;
}
