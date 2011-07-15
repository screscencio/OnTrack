package br.com.oncast.ontrack.server.util.converter.custom;

import java.util.List;

import br.com.oncast.ontrack.server.util.converter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.util.converter.TypeConverter;
import br.com.oncast.ontrack.server.util.converter.exceptions.TypeConverterException;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListConverter<T extends List> implements TypeConverter {

	private final Class<T> clazz;

	public ListConverter(final Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof List<?>)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not a List.");
		final List<?> list = (List<?>) originalBean;

		final T convertedList = createNewList();
		for (final Object item : list)
			convertedList.add(new GeneralTypeConverter().convert(item));

		return convertedList;
	}

	private T createNewList() throws TypeConverterException {
		T instance;
		try {
			instance = clazz.newInstance();
		}
		catch (final Exception e) {
			throw new TypeConverterException("It was not possible to instantiate a new list bean.");
		}
		return instance;
	}

}
