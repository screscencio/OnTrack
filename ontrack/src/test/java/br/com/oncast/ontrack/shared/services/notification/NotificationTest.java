package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.junit.Assert;
import org.junit.Test;

public class NotificationTest {

	@Test
	public void IMPORTANT_ShouldHaveTeamInvited() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.TEAM_INVITED);
		Assert.assertTrue(notification.isImportant(new UUID()));
	}

	@Test
	public void IMPORTANT_ShouldHaveTeamRemoved() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.TEAM_REMOVED);
		Assert.assertTrue(notification.isImportant(new UUID()));
	}

	@Test
	public void IMPORTANT_ShouldHaveImpedimentSolved() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.IMPEDIMENT_SOLVED);
		Assert.assertTrue(notification.isImportant(new UUID()));
	}

	@Test
	public void IMPORTANT_ShouldHaveImpedimentCreated() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.IMPEDIMENT_CREATED);
		Assert.assertTrue(notification.isImportant(new UUID()));
	}

	@Test
	public void IMPORTANT_ShouldNotHaveScopeAddAssociatedUser() {
		final Notification notification = new Notification();
		notification.setDescription("");
		notification.setType(NotificationType.SCOPE_ADD_ASSOCIATED_USER);
		Assert.assertFalse(notification.isImportant(new UUID()));
	}

	@Test
	public void IMPORTANT_ShouldHaveScopeAddAssociatedUserWhenTheAssociatedUserIdIsInDescription() {
		final Notification notification = new Notification();
		final UUID userId = new UUID();
		notification.setDescription(userId.toString());
		notification.setType(NotificationType.SCOPE_ADD_ASSOCIATED_USER);
		Assert.assertTrue(notification.isImportant(userId));
	}

	@Test
	public void Notification_ShouldKnowTheRecipientWhenTheCollectionHaveOneRecipient() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);
		Assert.assertEquals(recipient, notification.getRecipient(uuid));
	}

	@Test
	public void Notification_ShouldKnowTheRecipientWhenTheCollectionHaveTwoRecipients() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);
		final UUID otherUuid = new UUID();
		final NotificationRecipient otherRecipient = setUpNotificationWithRecipient(notification, otherUuid);
		notification.addRecipient(otherRecipient);
		Assert.assertEquals(recipient, notification.getRecipient(uuid));
	}

	@Test
	public void Notification_ShouldReturnRecipientAsUserIdsWhenTheCollectionHaveOneRecipient() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);
		Assert.assertTrue(notification.getRecipientsAsUserIds().contains(uuid));
	}

	@Test
	public void Notification_ShouldReturnRecipientAsUserIdsWhenTheCollectionHaveTwoRecipients() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);

		final UUID otherUuid = new UUID();
		final NotificationRecipient otherRecipient = setUpNotificationWithRecipient(notification, otherUuid);
		notification.addRecipient(otherRecipient);

		Assert.assertEquals(2, notification.getRecipientsAsUserIds().size());
		Assert.assertTrue(notification.getRecipientsAsUserIds().contains(uuid));
		Assert.assertTrue(notification.getRecipientsAsUserIds().contains(otherUuid));
	}

	@Test
	public void Notification_ShouldNotAddTheSameRecipientTwice() {
		final Notification notification = new Notification();
		final UUID uuid = new UUID();
		final NotificationRecipient recipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(recipient);

		final NotificationRecipient otherRecipient = setUpNotificationWithRecipient(notification, uuid);
		notification.addRecipient(otherRecipient);

		Assert.assertEquals(1, notification.getRecipientsAsUserIds().size());
		Assert.assertTrue(notification.getRecipientsAsUserIds().contains(uuid));
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