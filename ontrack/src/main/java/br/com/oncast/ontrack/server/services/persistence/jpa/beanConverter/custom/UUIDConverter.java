package br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.custom;

import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.TypeConverter;
import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.exceptions.BeanConverterException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UUIDConverter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws BeanConverterException {
		if (!(originalBean instanceof UUID)) throw new BeanConverterException("Cannot convert " + originalBean.getClass() + ": it is not an UUID.");
		return ((UUID) originalBean).toStringRepresentation();
	}

}
