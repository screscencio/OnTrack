package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.services.notification.Notification.NotificationType;

public class NotificationTypeConveter implements TypeConverter {

	@Override
	public Object convert(final Object originalBean) throws TypeConverterException {
		if (!(originalBean instanceof NotificationType)) throw new TypeConverterException("Cannot convert " + originalBean.getClass()
				+ ": it is not a Integer.");
		return originalBean;
	}

}
