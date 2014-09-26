package br.com.oncast.ontrack.shared.services.notification;

import org.junit.Assert;
import org.junit.Test;

public class NotificationTypeTest {

	@Test
	public void IMPEDIMENT_CREATED_ShouldReturnCorrectMessage() {
		Assert.assertEquals("criou o impedimento", NotificationType.IMPEDIMENT_CREATED.simpleMessage(new Notification()));
	}

	@Test
	public void IMPEDIMENT_SOLVED_ShouldReturnCorrectMessage() {
		Assert.assertEquals("resolveu o impedimento", NotificationType.IMPEDIMENT_SOLVED.simpleMessage(new Notification()));
	}

	@Test
	public void PROGRESS_DECLARED_ShouldReturnCorrectMessage() {
		Assert.assertEquals("declarou o progresso", NotificationType.PROGRESS_DECLARED.simpleMessage(new Notification()));
	}

	@Test
	public void ANNOTATION_CREATED_ShouldReturnCorrectMessage() {
		Assert.assertEquals("comentou", NotificationType.ANNOTATION_CREATED.simpleMessage(new Notification()));
	}

	@Test
	public void ANNOTATION_DEPRECATED_ShouldReturnCorrectMessage() {
		Assert.assertEquals("descartou", NotificationType.ANNOTATION_DEPRECATED.simpleMessage(new Notification()));
	}

	@Test
	public void TEAM_INVITED_ShouldReturnCorrectMessage() {
		final Notification notification = new Notification();
		notification.setReferenceDescription("OnCast");
		Assert.assertEquals("convidou o OnCast para o projeto", NotificationType.TEAM_INVITED.simpleMessage(notification));
	}

	@Test
	public void TEAM_REMOVED_ShouldReturnCorrectMessage() {
		final Notification notification = new Notification();
		notification.setReferenceDescription("OnCast");
		Assert.assertEquals("excluiu o OnCast do projeto", NotificationType.TEAM_REMOVED.simpleMessage(notification));
	}

	@Test
	public void SCOPE_BIND_HUMAN_ID_ShouldReturnCorrectMessage() {
		final Notification notification = new Notification();
		notification.setReferenceDescription("Item XPTO");
		Assert.assertEquals("te vinculou ao ítem Item XPTO", NotificationType.SCOPE_ADD_ASSOCIATED_USER.simpleMessage(notification));
	}
}