package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import java.util.HashSet;
import java.util.List;

import br.com.oncast.ontrack.server.utils.typeConverter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

@SuppressWarnings("rawtypes")
public class ListToHashSetConverter implements TypeConverter {

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof List<?>)) throw new TypeConverterException("Cannot convert " + originalBean.getClass()
				+ ": it is not a List.");
		final List list = (List) new GeneralTypeConverter().convert(originalBean);

		return new HashSet(list);

	}

}
