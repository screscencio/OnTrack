package br.com.oncast.ontrack.server.util.converter;

import br.com.oncast.ontrack.server.util.converter.exceptions.TypeConverterException;

public interface TypeConverter {
	Object convert(Object originalBean) throws TypeConverterException;
}
