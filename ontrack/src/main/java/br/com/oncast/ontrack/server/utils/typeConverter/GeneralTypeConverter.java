package br.com.oncast.ontrack.server.utils.typeConverter;

import br.com.oncast.ontrack.server.utils.typeConverter.baseConverters.BooleanConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.baseConverters.CollectionConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.baseConverters.DateConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.baseConverters.EnumConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.baseConverters.FloatConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.baseConverters.IntegerConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.baseConverters.LongConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.baseConverters.StringConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

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
		addCustomConverter(Enum.class, new EnumConverter());
		addCustomConverter(ArrayList.class, new CollectionConverter<ArrayList>(ArrayList.class));
		addCustomConverter(LinkedList.class, new CollectionConverter<LinkedList>(LinkedList.class));
		addCustomConverter(Date.class, new DateConverter());
	}

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		final TypeConverter converter = getConverter(originalBean);
		return converter.convert(originalBean);
	}

	private TypeConverter getConverter(final Object sourceBean) throws TypeConverterException {
		final Class<? extends Object> originClass = sourceBean.getClass();
		for (final Entry<Class<?>, TypeConverter> e : CUSTOM_CONVERTERS.entrySet()) {
			if (e.getKey().isAssignableFrom(originClass)) return e.getValue();
		}
		return DEFAULT_CONVERTER;
	}
}
