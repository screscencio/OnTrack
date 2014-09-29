package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NotificationBuilderTest {

	private final ProjectRepresentation projectRepresentation = ProjectTestUtils.createRepresentation();
	private final UUID authorId = new UUID();

	@Test
	public void shouldCreateNotificationAnnotationCreated() {
		final Notification notification = createNotification(NotificationType.ANNOTATION_CREATED);
		assertNormalNotification(notification, NotificationType.ANNOTATION_CREATED);
		assertEquals(NotificationType.ANNOTATION_CREATED, notification.getType());
	}

	@Test
	public void shouldCreateNotificationAnnotationDeprecated() {
		final Notification notification = createNotification(NotificationType.ANNOTATION_DEPRECATED);
		assertNormalNotification(notification, NotificationType.ANNOTATION_DEPRECATED);
	}

	@Test
	public void shouldCreateNotificationTeamInvited() {
		final Notification notification = createNotification(NotificationType.TEAM_INVITED);
		assertNormalNotification(notification, NotificationType.TEAM_INVITED);
	}

	@Test
	public void shouldCreateNotificationTeamRemoved() {
		final Notification notification = createNotification(NotificationType.TEAM_REMOVED);
		assertNormalNotification(notification, NotificationType.TEAM_REMOVED);
	}

	@Test
	public void shouldCreateNotificationImpedimentCreated() {
		final Notification notification = createNotification(NotificationType.IMPEDIMENT_CREATED);
		assertNormalNotification(notification, NotificationType.IMPEDIMENT_CREATED);
	}

	@Test
	public void shouldCreateNotificationImpedimentSolved() {
		final Notification notification = createNotification(NotificationType.IMPEDIMENT_SOLVED);
		assertNormalNotification(notification, NotificationType.IMPEDIMENT_SOLVED);
	}

	@Test
	public void shouldCreateNotificationProgressDeclared() {
		final Notification notification = createNotification(NotificationType.PROGRESS_DECLARED);
		assertNormalNotification(notification, NotificationType.PROGRESS_DECLARED);
	}

	@Test
	public void shouldCreateNotificationScopeAddedAssociatedUser() {
		final Notification notification = createNotification(NotificationType.SCOPE_ADD_ASSOCIATED_USER);
		assertNormalNotification(notification, NotificationType.SCOPE_ADD_ASSOCIATED_USER);
	}

	private void assertNormalNotification(final Notification notification, final NotificationType type) {
		assertNotNull(notification);
		assertEquals(authorId, notification.getAuthorId());
		assertEquals("", notification.getDescription());
		assertEquals(projectRepresentation.getId(), notification.getProjectId());
		assertEquals(projectRepresentation.getId(), notification.getProjectReference());
		assertEquals("", notification.getReferenceDescription());
		assertNull(notification.getReferenceId());
		assertNotNull(notification.getTimestamp());
		assertEquals(type, notification.getType());
	}

	private Notification createNotification(final NotificationType type) {
		final NotificationBuilder builder = new NotificationBuilder(type, projectRepresentation, authorId);
		final Notification notification = builder.getNotification();
		return notification;
	}
}