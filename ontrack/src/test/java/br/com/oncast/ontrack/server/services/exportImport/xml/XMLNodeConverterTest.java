package br.com.oncast.ontrack.server.services.exportImport.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.mocks.models.PasswordTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class XMLNodeConverterTest {

	@Test
	public void convertFromUsersToUserXMLNodesWithoutPassword() throws Exception {
		final List<User> users = UserTestUtils.createList(10);
		final List<UserXMLNode> userNodes = XMLNodeConverter.getUserNodes(users, new ArrayList<Password>());
		for (int i = 0; i < users.size(); i++) {
			assertEquals(users.get(i).getId(), userNodes.get(i).getId());
			assertEquals(users.get(i).getEmail(), userNodes.get(i).getEmail());
			assertFalse(userNodes.get(i).hasPassword());
		}
	}

	@Test
	public void convertFromUsersToUserXMLNodesWithPassword() throws Exception {
		final List<User> users = UserTestUtils.createList(10);
		final List<Password> passwords = PasswordTestUtils.createPasswordsFor(users);

		final List<UserXMLNode> userNodes = XMLNodeConverter.getUserNodes(users, new ArrayList<Password>());
		for (int i = 0; i < users.size(); i++) {
			assertEquals(users.get(i).getId(), userNodes.get(i).getId());
			assertEquals(users.get(i).getEmail(), userNodes.get(i).getEmail());
			assertFalse(userNodes.get(i).hasPassword());
		}
	}

}
