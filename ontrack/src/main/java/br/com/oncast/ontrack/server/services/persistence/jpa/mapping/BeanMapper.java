package br.com.oncast.ontrack.server.services.persistence.jpa.mapping;

import java.util.HashMap;
import java.util.Map;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.exceptions.BeanMapperException;

public class BeanMapper implements BeanTypeMapper {

	private final static GenericTypeMapper DEFAULT_MAPPER = new GenericTypeMapper();
	public final static Map<Class<?>, BeanTypeMapper> CUSTOM_MAPPERS = new HashMap<Class<?>, BeanTypeMapper>();

	public static void addCustomBeanTypeMapper(final Class<?> originClass, final BeanTypeMapper customMapper) {
		CUSTOM_MAPPERS.put(originClass, customMapper);
	}

	private boolean hasCustomMapper(final Object sourceBean) {
		return (CUSTOM_MAPPERS.containsKey(sourceBean.getClass()));
	}

	private BeanTypeMapper getCustomMapper(final Object sourceBean) throws BeanMapperException {
		final Class<? extends Object> originClass = sourceBean.getClass();

		if (!CUSTOM_MAPPERS.containsKey(originClass)) throw new BeanMapperException("It was not possible to locate a custom TypeMapper.");
		return CUSTOM_MAPPERS.get(originClass);
	}

	@Override
	public Object createMappedBean(final Object originalBean) throws BeanMapperException {
		final BeanTypeMapper mapper = (hasCustomMapper(originalBean)) ? getCustomMapper(originalBean) : DEFAULT_MAPPER;
		return mapper.createMappedBean(originalBean);
	}
}
