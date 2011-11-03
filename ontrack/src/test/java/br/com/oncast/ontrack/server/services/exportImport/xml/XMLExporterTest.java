package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import br.com.oncast.ontrack.server.business.UserAction;
import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.services.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.setup.DefaultUserExistenceAssurer;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.user.User;

public class XMLExporterTest {

	private final PersistenceService persistenceService = ServerServiceProvider.getInstance().getPersistenceService();

	// This test retrieve all database to ontrack.xml file, must be removed after.
	@Test
	@Ignore
	public void test() {
		DefaultUserExistenceAssurer.verify();
		final XMLExporter loader = new XMLExporter(persistenceService);
		loader.mountXML().export();
	}

	// This test persists all objects from ontrack.xml.
	@Test
	@Ignore
	public void test2() throws Exception {
		final Serializer serializer = new Persister();
		final File source = new File("ontrack.xml");

		final OntrackXML ontrackXML = serializer.read(OntrackXML.class, source);

		final List<User> userList = ontrackXML.getUsers();
		final List<Password> passwordList = ontrackXML.getPasswords();
		final List<UserAction> userActions = ontrackXML.getUserActions();

		for (final UserAction userAction : userActions) {
			final ArrayList<ModelAction> arrayList = new ArrayList<ModelAction>();
			arrayList.add(userAction.getModelAction());
			persistenceService.persistActions(arrayList, userAction.getTimestamp());
		}

		for (final User user : userList) {
			persistenceService.persistOrUpdateUser(user);
		}

		for (final Password pass : passwordList) {
			persistenceService.persistOrUpdatePassword(pass);
		}

	}
}
