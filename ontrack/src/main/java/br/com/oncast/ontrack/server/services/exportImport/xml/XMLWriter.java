package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.File;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import br.com.oncast.ontrack.server.business.UserAction;
import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.shared.model.user.User;

public class XMLWriter {

	private static final String XML_FILE = "ontrack.xml";
	private final OntrackXML ontrackXML;

	public XMLWriter() {
		ontrackXML = new OntrackXML();
	}

	public XMLWriter setUserList(final List<User> userList) {
		ontrackXML.setUsers(userList);
		return this;
	}

	public XMLWriter setPasswordList(final List<Password> passwordList) {
		ontrackXML.setPasswords(passwordList);
		return this;
	}

	public XMLWriter setVersion(final long version) {
		ontrackXML.setVersion(version);
		return this;
	}

	public XMLWriter setActionList(final List<UserAction> userActionList) {
		ontrackXML.setUserActions(userActionList);
		return this;
	}

	public void export() {
		final Serializer serializer = new Persister();
		final File result = new File(XML_FILE);

		try {
			serializer.write(ontrackXML, result);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
