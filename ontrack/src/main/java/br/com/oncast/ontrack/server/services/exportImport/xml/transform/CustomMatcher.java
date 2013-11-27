package br.com.oncast.ontrack.server.services.exportImport.xml.transform;

import br.com.oncast.ontrack.server.services.exportImport.xml.transformations.NotificationTypeTransform;
import br.com.oncast.ontrack.shared.services.notification.NotificationType;

import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;

public class CustomMatcher implements Matcher {

	@SuppressWarnings("rawtypes")
	@Override
	public Transform match(final Class clazz) throws Exception {
		if (NotificationType.class.isAssignableFrom(clazz)) return new NotificationTypeTransform();
		return null;
	}
}
