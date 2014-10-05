package br.com.oncast.ontrack.shared.utils;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.utils.AnnotationDescriptionParser.ParseHandler;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AnnotationDescriptionParserTest {

	static final String SYMBOL = "@";
	UserParseHandler handler;

	@Before
	public void setUp() {
		handler = new UserParseHandler();
	}

	@Test
	public void shouldReplaceUserUUIDByItsName() {
		final String userName = "My name";
		final User user = createUser(userName);
		final String text = reference(user) + " fez tal coisa";
		final String resultHtml = AnnotationDescriptionParser.parse(text, Arrays.asList(user), handler);
		Assert.assertTrue(resultHtml.contains(userName));
	}

	@Test
	public void shouldReplaceMultipleUserUUIDByItsName() {
		final String userName = "My name";
		final User user = createUser(userName);

		final String otherUserName = "Other name";
		final User otherUser = createUser(otherUserName);

		final String text = reference(user) + " fez tal coisa com o usuário " + reference(otherUser);
		final String resultHtml = AnnotationDescriptionParser.parse(text, Arrays.asList(user, otherUser), handler);
		Assert.assertEquals(user.getName() + " fez tal coisa com o usuário " + otherUser.getName(), resultHtml);
	}

	@Test
	public void shouldSkipNonMatchingCases() {
		final String userName = "My name";
		final User user = createUser(userName);

		final String otherUserName = "Other name";
		final User otherUser = createUser(otherUserName);

		final String text = reference(user) + " fez " + SYMBOL + "tal coisa com " + SYMBOL + " o usuário " + reference(otherUser);
		final String resultHtml = AnnotationDescriptionParser.parse(text, Arrays.asList(user, otherUser), handler);
		Assert.assertEquals(user.getName() + " fez @tal coisa com @ o usuário " + otherUser.getName(), resultHtml);
	}

	private class UserParseHandler implements ParseHandler<User> {

		@Override
		public String getReplacement(final User model) {
			return model.getName();
		}

	}

	private String reference(final User user) {
		return SYMBOL + user.getId();
	}

	private User createUser(final String userName) {
		final User user = UserTestUtils.createUser();
		user.setName(userName);
		return user;
	}

}
