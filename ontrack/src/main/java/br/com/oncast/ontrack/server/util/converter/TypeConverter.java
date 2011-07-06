package br.com.oncast.ontrack.server.util.converter;

import br.com.oncast.ontrack.shared.exceptions.converter.TypeConverterException;

public interface TypeConverter {
	Object convert(Object originalBean) throws TypeConverterException;
}
