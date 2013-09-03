package br.com.oncast.ontrack.server.utils.typeConverter.baseConverters;

import br.com.oncast.ontrack.server.utils.typeConverter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

import java.util.List;

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
		} catch (final Exception e) {
			throw new TypeConverterException("It was not possible to instantiate a new list bean.");
		}
		return instance;
	}

}
