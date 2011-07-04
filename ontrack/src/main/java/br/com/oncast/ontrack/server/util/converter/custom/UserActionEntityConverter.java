package br.com.oncast.ontrack.server.util.converter.custom;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.UserActionEntity;
import br.com.oncast.ontrack.server.util.converter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.util.converter.TypeConverter;
import br.com.oncast.ontrack.shared.exceptions.converter.BeanConverterException;

public class UserActionEntityConverter implements TypeConverter {

	private static final GeneralTypeConverter GENERAL_TYPE_CONVERTER = new GeneralTypeConverter();

	@Override
	public Object convert(final Object originalBean) throws BeanConverterException {
		if (!(originalBean instanceof UserActionEntity)) throw new BeanConverterException("Cannot convert " + originalBean.getClass() + ": it is not a "
				+ UserActionEntity.class.getName() + ".");
		return GENERAL_TYPE_CONVERTER.convert(((UserActionEntity) originalBean).getActionEntity());
	}
}
