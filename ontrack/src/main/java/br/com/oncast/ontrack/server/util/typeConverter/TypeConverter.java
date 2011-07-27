package br.com.oncast.ontrack.server.util.typeConverter;

import br.com.oncast.ontrack.server.util.typeConverter.exceptions.TypeConverterException;

public interface TypeConverter {
	Object convert(Object originalBean) throws TypeConverterException;
}
