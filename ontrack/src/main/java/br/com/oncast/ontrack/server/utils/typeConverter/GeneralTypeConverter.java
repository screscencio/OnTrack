package br.com.oncast.ontrack.server.utils.typeConverter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.hibernate.collection.PersistentBag;

import br.com.oncast.ontrack.server.utils.typeConverter.custom.BooleanConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.DateConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.FloatConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.IntegerConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.ListConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.LongConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.UUIDConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@SuppressWarnings("rawtypes")
public class GeneralTypeConverter implements TypeConverter {

	private final static AnnotationBasedTypeConverter DEFAULT_CONVERTER = new AnnotationBasedTypeConverter();
	private final static Map<Class<?>, TypeConverter> CUSTOM_CONVERTERS = new HashMap<Class<?>, TypeConverter>();

	public static void addCustomConverter(final Class<?> originClass, final TypeConverter customConverter) {
		CUSTOM_CONVERTERS.put(originClass, customConverter);
	}

	static {
		addCustomConverter(Boolean.class, new BooleanConverter());
		addCustomConverter(Integer.class, new IntegerConverter());
		addCustomConverter(Float.class, new FloatConverter());
		addCustomConverter(Long.class, new LongConverter());
		addCustomConverter(String.class, new StringConverter());
		addCustomConverter(ArrayList.class, new ListConverter<ArrayList>(ArrayList.class));
		addCustomConverter(LinkedList.class, new ListConverter<LinkedList>(LinkedList.class));
		addCustomConverter(HashSet.class, new CollectionToListConverter<ArrayList>(ArrayList.class));

		// IMPORTANT Date is the superclass of Timestamp, so the same converter is really used for both.
		addCustomConverter(Date.class, new DateConverter());
		addCustomConverter(Timestamp.class, new DateConverter());

		// TODO +Externalize this so that specific application converters are registered by the application itself.
		addCustomConverter(UUID.class, new UUIDConverter());
		addCustomConverter(PersistentBag.class, new ListConverter<ArrayList>(ArrayList.class));
	}

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		final TypeConverter converter = (hasCustomConverter(originalBean)) ? getCustomConverter(originalBean) : DEFAULT_CONVERTER;
		return converter.convert(originalBean);
	}

	private boolean hasCustomConverter(final Object sourceBean) {
		return (CUSTOM_CONVERTERS.containsKey(sourceBean.getClass()));
	}

	private TypeConverter getCustomConverter(final Object sourceBean) throws TypeConverterException {
		final Class<? extends Object> originClass = sourceBean.getClass();

		if (!CUSTOM_CONVERTERS.containsKey(originClass)) throw new TypeConverterException("It was not possible to locate a custom TypeConverter.");
		return CUSTOM_CONVERTERS.get(originClass);
	}
}
