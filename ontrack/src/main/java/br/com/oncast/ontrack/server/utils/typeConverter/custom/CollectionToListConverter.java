package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import java.util.Collection;
import java.util.List;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

@SuppressWarnings("rawtypes")
public class CollectionToListConverter<T extends List> implements TypeConverter {

	private final Class<T> clazz;

	public CollectionToListConverter(final Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof Collection<?>)) throw new TypeConverterException("Cannot convert " + originalBean.getClass()
				+ ": it is not a Collection.");

		try {
			return new ListConverter<T>(clazz).convert(createNewList((Collection) originalBean));
		}
		catch (final Exception e) {
			throw new TypeConverterException("Cannot convert " + originalBean.getClass() + ": Reflection Problem", e);
		}
	}

	private List createNewList(final Collection originalBean) throws TypeConverterException {
		List instance;
		try {
			instance = this.clazz.getDeclaredConstructor(Collection.class).newInstance(originalBean);
		}
		catch (final Exception e) {
			throw new TypeConverterException("It was not possible to instantiate a new list bean.");
		}
		return instance;
	}

}
