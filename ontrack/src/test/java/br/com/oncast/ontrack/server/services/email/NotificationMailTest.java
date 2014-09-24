package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationType;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

public class NotificationMailTest {

	@Mock
	private Notification notification;

	private NotificationMail mail;

	private User recipient;

	private User author;

	private ProjectRepresentation project;

	private List<User> users;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		author = UserTestUtils.createUser("author@mail.com");
		recipient = UserTestUtils.createUser("recipient@mail.com");
		project = ProjectTestUtils.createRepresentation();
		mail = NotificationMail.getMail(notification, author, recipient, new ArrayList<User>(), project);
		when(notification.getReferenceId()).thenReturn(new UUID());
		when(notification.getDescription()).thenReturn("my notification description");
		when(notification.getReferenceDescription()).thenReturn("my notification reference description");
		when(notification.getType()).thenReturn(NotificationType.TEAM_INVITED);
	}

	@Test
	public void testMailSubject() throws Exception {
		assertEquals("[OnTrack] Notifição", mail.getSubject());
	}

	@Test
	public void testAnnotationMailTemplatePath() throws Exception {
		when(notification.getType()).thenReturn(NotificationType.IMPEDIMENT_CREATED);
		assertEquals("/br/com/oncast/ontrack/server/services/email/annotationNotificationMail.html", mail.getTemplatePath());
	}

	@Test
	public void testInvitationMailTemplatePath() throws Exception {
		when(notification.getType()).thenReturn(NotificationType.TEAM_INVITED);
		assertEquals("/br/com/oncast/ontrack/server/services/email/invitationNotificationMail.html", mail.getTemplatePath());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBeAbleToSendNotificationWithoutRecipients() throws Exception {
		NotificationMail.getMail(notification, author, null, users, project);
	}

	@Test
	public void shouldSendMailToAllNotificationRecipients() throws Exception {
		assertEquals(Arrays.asList(recipient.getEmail()), mail.getRecipients());
	}

	@Test
	public void shouldHaveCorrectParameters() throws Exception {
		assertEquals(recipient.getName(), mail.getParameters().get("userName"));
		assertEquals(project.getId(), mail.getParameters().get("projectId"));
		assertEquals(project.getName(), mail.getParameters().get("projectName"));
		assertEquals(author.getName(), mail.getParameters().get("authorName"));
		assertEquals(notification.getReferenceId(), mail.getParameters().get("annotationId"));
		assertEquals(notification.getDescription(), mail.getParameters().get("annotationDescription"));
		assertEquals(notification.getReferenceDescription(), mail.getParameters().get("subjectDescription"));
		assertEquals(notification.getType().simpleMessage(notification), mail.getParameters().get("action"));
	}

}
