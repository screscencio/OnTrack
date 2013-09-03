package br.com.oncast.ontrack.server.utils.typeConverter.baseConverters;

import br.com.oncast.ontrack.server.utils.typeConverter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

import java.util.Collection;

@SuppressWarnings({ "unchecked", "rawtypes" })
// TODO+ remove rawtypes from Collection
public class CollectionConverter<T extends Collection> implements TypeConverter {

	private final Class<T> clazz;

	public CollectionConverter(final Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof Collection<?>)) throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": it is not a Collection.");
		final Collection<?> collection = (Collection<?>) originalBean;

		final T convertedCollection = createNewCollection();
		for (final Object item : collection)
			convertedCollection.add(new GeneralTypeConverter().convert(item));

		return convertedCollection;
	}

	private T createNewCollection() throws TypeConverterException {
		T instance;
		try {
			instance = clazz.newInstance();
		} catch (final Exception e) {
			throw new TypeConverterException("It was not possible to instantiate a new list bean.");
		}
		return instance;
	}

}
