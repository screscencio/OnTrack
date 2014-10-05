package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationDeprecateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.TeamRevogueInvitationAction;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NotificationBuilderTest {

	private final ProjectRepresentation projectRepresentation = ProjectTestUtils.createRepresentation();
	private final UUID authorId = new UUID();
	private final UUID subjectId = new UUID();
	private final UUID userId = new UUID();
	private final UUID annotationId = new UUID();
	private final UUID referenceId = new UUID();

	@Test
	public void shouldCreateNotificationAnnotationCreated() {
		final UUID subjectId = new UUID();
		final UUID attachmentId = new UUID();
		final AnnotationCreateAction action = new AnnotationCreateAction(subjectId, AnnotationType.SIMPLE, "message", attachmentId);
		final Notification notification = createNotification(NotificationType.ANNOTATION_CREATED, action);
		assertNormalNotification(notification, NotificationType.ANNOTATION_CREATED);
		assertEquals(subjectId, notification.getReferenceId());
	}

	@Test
	public void shouldCreateNotificationAnnotationDeprecated() {
		final AnnotationDeprecateAction action = new AnnotationDeprecateAction(subjectId, annotationId);
		final Notification notification = createNotification(NotificationType.ANNOTATION_DEPRECATED, action);
		assertNormalNotification(notification, NotificationType.ANNOTATION_DEPRECATED);
		assertEquals(subjectId, notification.getReferenceId());
	}

	@Test
	public void shouldCreateNotificationTeamInvited() {
		final TeamInviteAction action = new TeamInviteAction(userId, Profile.ACCOUNT_MANAGER);
		final Notification notification = createNotification(NotificationType.TEAM_INVITED, action);
		assertNormalNotification(notification, NotificationType.TEAM_INVITED);
		assertEquals(userId, notification.getReferenceId());
	}

	@Test
	public void shouldCreateNotificationTeamRemoved() {
		final TeamRevogueInvitationAction action = new TeamRevogueInvitationAction(userId);
		final Notification notification = createNotification(NotificationType.TEAM_REMOVED, action);
		assertNormalNotification(notification, NotificationType.TEAM_REMOVED);
		assertEquals(userId, notification.getReferenceId());
	}

	@Test
	public void shouldCreateNotificationImpedimentCreated() {
		final ImpedimentCreateAction action = new ImpedimentCreateAction(subjectId, annotationId);
		final Notification notification = createNotification(NotificationType.IMPEDIMENT_CREATED, action);
		assertNormalNotification(notification, NotificationType.IMPEDIMENT_CREATED);
		assertEquals(subjectId, notification.getReferenceId());
	}

	@Test
	public void shouldCreateNotificationImpedimentSolved() {
		final ImpedimentSolveAction action = new ImpedimentSolveAction(subjectId, annotationId);
		final Notification notification = createNotification(NotificationType.IMPEDIMENT_SOLVED, action);
		assertNormalNotification(notification, NotificationType.IMPEDIMENT_SOLVED);
		assertEquals(subjectId, notification.getReferenceId());
	}

	@Test
	public void shouldCreateNotificationProgressDeclared() {
		final ScopeDeclareProgressAction action = new ScopeDeclareProgressAction(referenceId, "Progress on the text");
		final NotificationBuilder builder = new NotificationBuilder(NotificationType.PROGRESS_DECLARED, projectRepresentation, authorId).setReferenceId(action.getReferenceId());
		final UserRepresentation userRepresentation = new UserRepresentation(authorId);
		final Scope scope = new Scope("Scope description", userRepresentation, new Date());

		builder.setReferenceDescription(scope.getDescription());
		builder.setDescription(scope.getProgress().getDescription());

		final Notification notification = builder.getNotification();
		assertNormalNotification(notification, NotificationType.PROGRESS_DECLARED);

		assertEquals(referenceId, notification.getReferenceId());
		assertEquals("Scope description", notification.getReferenceDescription());
	}

	@Test
	public void shouldCreateNotificationScopeAddedAssociatedUser() {
		final UserRepresentation userRepresentation = new UserRepresentation(authorId);
		final Scope scope = new Scope("Scope description", userRepresentation, new Date());
		final ScopeAddAssociatedUserAction action = new ScopeAddAssociatedUserAction(scope.getId(), userId);
		final NotificationBuilder builder = new NotificationBuilder(NotificationType.SCOPE_ADD_ASSOCIATED_USER, projectRepresentation, authorId).setReferenceId(action.getReferenceId());
		builder.setReferenceDescription(scope.getDescription());
		builder.setDescription(action.getUserId().toString());

		final Notification notification = builder.getNotification();

		assertNormalNotification(notification, NotificationType.SCOPE_ADD_ASSOCIATED_USER);

		assertEquals(action.getReferenceId(), notification.getReferenceId());
		assertEquals("Scope description", notification.getReferenceDescription());
		assertEquals(userId.toString(), notification.getDescription());
	}

	private void assertNormalNotification(final Notification notification, final NotificationType type) {
		assertNotNull(notification);
		assertEquals(authorId, notification.getAuthorId());
		assertEquals(projectRepresentation.getId(), notification.getProjectId());
		assertEquals(projectRepresentation.getId(), notification.getProjectReference());

		assertNotNull(notification.getTimestamp());
		assertEquals(type, notification.getType());
	}

	private Notification createNotification(final NotificationType type, final ModelAction action) {
		final NotificationBuilder builder = new NotificationBuilder(type, projectRepresentation, authorId).setReferenceId(action.getReferenceId());
		final Notification notification = builder.getNotification();
		return notification;
	}
}