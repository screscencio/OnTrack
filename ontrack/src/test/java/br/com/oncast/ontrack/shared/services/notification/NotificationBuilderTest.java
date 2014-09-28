package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NotificationBuilderTest {

	@Test
	public void shouldCreateNotificationAnnotationCreated() {
		final ProjectRepresentation projectRepresentation = ProjectTestUtils.createRepresentation();
		final UUID authorId = new UUID();
		final NotificationBuilder builder = new NotificationBuilder(NotificationType.ANNOTATION_CREATED, projectRepresentation, authorId);
		final Notification notification = builder.getNotification();
		assertNotNull(notification);
		assertEquals(authorId, notification.getAuthorId());
		assertEquals("", notification.getDescription());
		assertEquals(projectRepresentation.getId(), notification.getProjectId());
		assertEquals(projectRepresentation.getId(), notification.getProjectReference());
		assertEquals("", notification.getReferenceDescription());
		assertNull(notification.getReferenceId());
		assertEquals(new Date(), notification.getTimestamp());
		assertEquals(NotificationType.ANNOTATION_CREATED, notification.getType());
	}
}