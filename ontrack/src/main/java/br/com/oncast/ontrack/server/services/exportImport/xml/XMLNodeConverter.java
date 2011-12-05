package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.shared.model.user.User;

public class XMLNodeConverter {

	public static List<UserXMLNode> getUserNodes(final List<User> users, final ArrayList<Password> passwords) {
		final List<UserXMLNode> userNodes = new ArrayList<UserXMLNode>();
		for (final User user : users) {
			userNodes.add(new UserXMLNode(user.getId(), user.getEmail()));
		}
		return userNodes;
	}

}
