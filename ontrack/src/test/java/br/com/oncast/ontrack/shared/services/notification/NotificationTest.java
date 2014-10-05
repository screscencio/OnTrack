package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NotificationTest {

	@Test
	public void notificationTeamInvited_ShouldBeImportant() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.TEAM_INVITED);
		assertTrue(notification.isImportant(new UUID()));
	}

	@Test
	public void notificationTeamRemoved_ShouldBeImportant() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.TEAM_REMOVED);
		assertTrue(notification.isImportant(new UUID()));
	}

	@Test
	public void notificationImpedimentSolved_ShouldBeImportant() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.IMPEDIMENT_SOLVED);
		assertTrue(notification.isImportant(new UUID()));
	}

	@Test
	public void notificationImpedimentCreated_ShouldBeImportant() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.IMPEDIMENT_CREATED);
		assertTrue(notification.isImportant(new UUID()));
	}

	@Test
	public void notificationScopeAddAssociatedUser_WhenTheAssociatedUserIdIsInDescription_ShouldBeImportant() {
		final Notification notification = new Notification();
		final UUID userId = new UUID();
		notification.setDescription(userId.toString());
		notification.setType(NotificationType.SCOPE_ADD_ASSOCIATED_USER);
		assertTrue(notification.isImportant(userId));
	}

	@Test
	public void notificationScopeAddAssociatedUser_ShouldNotBeImportant() {
		final Notification notification = new Notification();
		notification.setDescription("");
		notification.setType(NotificationType.SCOPE_ADD_ASSOCIATED_USER);
		assertFalse(notification.isImportant(new UUID()));
	}

	@Test
	public void notificationAnnotationDeprecated_WhenHaveNoMentions_ShouldNotBeImportant() {
		final Notification notification = new Notification();
		notification.setDescription("");
		notification.setType(NotificationType.ANNOTATION_DEPRECATED);
		assertFalse(notification.isImportant(new UUID()));
	}

	@Test
	public void notificationAnnotationDeprecated_WhenHaveMentions_ShouldBeImportant() {
		final Notification notification = new Notification();
		final UUID userId = new UUID();
		notification.setDescription(userId.toString());
		notification.setType(NotificationType.ANNOTATION_DEPRECATED);
		assertTrue(notification.isImportant(userId));
	}

	@Test
	public void notificationAnnotationCreated_WhenHaveNoMentions_ShouldNotBeImportant() {
		final Notification notification = new Notification();
		notification.setDescription("");
		notification.setType(NotificationType.ANNOTATION_CREATED);
		assertFalse(notification.isImportant(new UUID()));
	}

	@Test
	public void notificationAnnotationCreated_WhenHaveMentions_ShouldBeImportant() {
		final Notification notification = new Notification();
		final UUID userId = new UUID();
		notification.setDescription(userId.toString());
		notification.setType(NotificationType.ANNOTATION_CREATED);
		assertTrue(notification.isImportant(userId));
	}

	@Test
	public void notificationProgressDeclared_WhenHaveNoMentions_ShouldNotBeImportant() {
		final Notification notification = new Notification();
		notification.setDescription("");
		notification.setType(NotificationType.PROGRESS_DECLARED);
		assertFalse(notification.isImportant(new UUID()));
	}

	@Test
	public void notificationProgressDeclared_WhenHaveMentions_ShouldBeImportant() {
		final Notification notification = new Notification();
		final UUID userId = new UUID();
		notification.setDescription(userId.toString());
		notification.setType(NotificationType.PROGRESS_DECLARED);
		assertTrue(notification.isImportant(userId));
	}

	@Test
	public void notification_ShouldKnowTheRecipient_WhenTheCollectionHaveOneRecipient() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);
		assertEquals(recipient, notification.getRecipient(uuid));
	}

	@Test
	public void notification_ShouldKnowTheRecipient_WhenTheCollectionHaveTwoRecipients() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);
		final UUID otherUuid = new UUID();
		final NotificationRecipient otherRecipient = setUpNotificationWithRecipient(notification, otherUuid);
		notification.addRecipient(otherRecipient);
		assertEquals(recipient, notification.getRecipient(uuid));
	}

	@Test
	public void notification_ShouldReturnRecipientAsUserIds_WhenTheCollectionHaveOneRecipient() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);
		assertTrue(notification.getRecipientsAsUserIds().contains(uuid));
	}

	@Test
	public void notification_ShouldReturnRecipientAsUserIds_WhenTheCollectionHaveTwoRecipients() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);

		final UUID otherUuid = new UUID();
		final NotificationRecipient otherRecipient = setUpNotificationWithRecipient(notification, otherUuid);
		notification.addRecipient(otherRecipient);

		assertEquals(2, notification.getRecipientsAsUserIds().size());
		assertTrue(notification.getRecipientsAsUserIds().contains(uuid));
		assertTrue(notification.getRecipientsAsUserIds().contains(otherUuid));
	}

	@Test
	public void notification_ShouldNotAddTheSameRecipientTwice() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);

		final NotificationRecipient otherRecipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(otherRecipient);

		assertEquals(1, notification.getRecipientsAsUserIds().size());
		assertTrue(notification.getRecipientsAsUserIds().contains(uuid));
	}

	@Test
	public void shouldReturnCorrectStringOnToString() {
		final Notification notification = new Notification();
		notification.setDescription("XPTO");
		assertEquals("Notification(XPTO)", notification.toString());
	}

	private NotificationRecipient setUpNotificationWithRecipient(final Notification notification, final UUID uuid) {
		final NotificationRecipient recipient = new NotificationRecipient();
		recipient.setUserId(uuid);
		recipient.setId(uuid);
		final NotificationRecipient recipientInside = new NotificationRecipient();
		recipientInside.setUserId(uuid);
		return recipient;
	}
}