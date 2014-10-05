package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationType;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

public class MailSenderTest {

	@Mock
	private Notification notification;

	private User recipient;
	private User author;
	private ProjectRepresentation project;

	private OnTrackMail mail;

	private MailSender sender;

	@Before
	public void setup() throws AddressException, MessagingException {
		MockitoAnnotations.initMocks(this);
		author = UserTestUtils.createUser("author@mail.com");
		recipient = UserTestUtils.createUser("recipient@mail.com");
		project = ProjectTestUtils.createRepresentation();
		mail = NotificationMail.getMail(notification, author, recipient, new ArrayList<User>(), project);
		when(notification.getType()).thenReturn(NotificationType.IMPEDIMENT_CREATED);
		sender = MailSender.getSender(mail);
	}

	@Test
	public void shouldSendWithTheSameSubject() throws AddressException, MessagingException {
		final MimeMessage message = sender.send();
		assertEquals(mail.getSubject(), message.getSubject());
	}

	@Test
	public void shouldSendToOneRecipients() throws AddressException, MessagingException {
		final MimeMessage message = sender.send();
		assertEquals(1, message.getAllRecipients().length);
		assertEquals("recipient@mail.com", message.getAllRecipients()[0].toString());
	}
}