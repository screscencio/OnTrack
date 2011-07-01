package br.com.oncast.ontrack.server.services.persistence.jpa.mapping;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.exceptions.BeanMapperException;

public interface BeanTypeMapper {
	Object createMappedBean(Object originalBean) throws BeanMapperException;
}
