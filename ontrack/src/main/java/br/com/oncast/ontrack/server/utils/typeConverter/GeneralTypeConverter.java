package br.com.oncast.ontrack.server.utils.typeConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.collection.PersistentBag;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.UserActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.BooleanConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.IntegerConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.ListConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.UUIDConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.UserActionEntityConverter;
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
		addCustomConverter(String.class, new StringConverter());
		addCustomConverter(Integer.class, new IntegerConverter());
		addCustomConverter(ArrayList.class, new ListConverter<ArrayList>(ArrayList.class));

		// TODO +Externalize this so that specific application converters are registered by the application itself.
		addCustomConverter(UUID.class, new UUIDConverter());
		addCustomConverter(UserActionEntity.class, new UserActionEntityConverter());
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