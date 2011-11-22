package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertAttributeDoesntExist;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertAttributeExists;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementAttributeValueIs;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementDoesntExist;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementExists;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementHasTheseAttributesAndNothingElse;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementsType;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.getElement;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.getElements;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.readXMLFromFile;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.util.migration.MigrationTestUtils;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class Migration_2011_11_18_Test {

	private Document sourceDocument;
	private Migration migration;

	@Before
	public void setUp() throws Exception {
		sourceDocument = readXMLFromFile("ontrack_2011_11_01.xml");
		migration = new Migration_2011_11_18();
	}

	@Test
	public void shouldMergePasswordsIntoTheirRelatedUser() throws Exception {
		final String xPath = "/ontrackXML/users/*";
		final List<Element> users = getElements(sourceDocument, xPath);

		final Map<String, String[]> usersAndPasswords = new java.util.HashMap<String, String[]>();
		for (final Element user : users) {
			final String userId = user.attributeValue("id");

			final Element password = getElement(sourceDocument, "/ontrackXML/passwords/*[@userId='" + userId + "']");
			final String[] passwordHashAndSalt = { password.attributeValue("passwordHash"), password.attributeValue("passwordSalt") };
			usersAndPasswords.put(user.attributeValue("email"), passwordHashAndSalt);
		}

		migration.apply(sourceDocument);

		final List<Element> modifiedUsers = getElements(sourceDocument, xPath);
		for (final Element user : modifiedUsers) {
			final String[] password = usersAndPasswords.get(user.attributeValue("email"));
			if (password != null) {
				assertEquals(password[0], user.attributeValue("passwordHash"));
				assertEquals(password[1], user.attributeValue("passwordSalt"));
			}
		}

		assertElementDoesntExist(sourceDocument, "/ontrackXML/passwords");
	}

	@Test
	public void userShouldNotHavePasswordHashAndSaltAttributesWhenItDoesntHaveARelatedPassword() throws Exception {
		removePasswordOfUser(1);

		migration.apply(sourceDocument);

		final Element userWithoutPassword = getElement(sourceDocument, "/ontrackXML/users/*[@email='user1@email']");
		final Element userWithPassword = getElement(sourceDocument, "/ontrackXML/users/*[@email='user2@email']");

		assertElementHasTheseAttributesAndNothingElse(userWithoutPassword, "email");
		assertElementHasTheseAttributesAndNothingElse(userWithPassword, "email", "passwordHash", "passwordSalt");
	}

	private void removePasswordOfUser(final int userId) {
		final Element password = getElement(sourceDocument, "/ontrackXML/passwords/*[@userId='" + userId + "']");
		password.detach();
	}

	@Test
	public void shouldRemoveIdAttributeFromUser() throws Exception {
		assertAttributeExists(sourceDocument, "//user/@id");

		migration.apply(sourceDocument);

		assertAttributeDoesntExist(sourceDocument, "//user/@id");
		assertElementExists(sourceDocument, "//user");
	}

	@Test
	public void shouldCreateProjectsList() throws Exception {
		final String xPath = "/ontrackXML/projects";
		assertElementDoesntExist(sourceDocument, xPath);
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, xPath);
		assertElementsType(sourceDocument, ArrayList.class, xPath);
	}

	@Test
	public void shouldCreateDefaultProjectInsideProjectsList() throws Exception {
		final String xPath = "/ontrackXML/projects/project";
		assertElementDoesntExist(sourceDocument, xPath);
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, xPath);
		assertElementsType(sourceDocument, ProjectXMLNode.class, xPath);
	}

	@Test
	public void shouldCreateDefaultProjectRepresentationAsAChildElementOfDefaultProject() throws Exception {
		final String xPath = "//project/projectRepresentation";
		assertElementDoesntExist(sourceDocument, xPath);
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, xPath);
		assertElementsType(sourceDocument, ProjectRepresentation.class, xPath);
	}

	@Test
	public void theDefaultProjectRepresentationShouldHaveOnlyTheDefaultNameAsAttribute() throws Exception {
		migration.apply(sourceDocument);

		final Element projectRepresentation = MigrationTestUtils.getElement(sourceDocument, "//projectRepresentation");
		assertElementHasTheseAttributesAndNothingElse(projectRepresentation, "name");
		assertElementAttributeValueIs(projectRepresentation, "name", "Project");
	}

	@Test
	public void shouldCreateActionsListAsAChildElementOfTheDefaultProject() throws Exception {
		final String xPath = "//project/actions";
		assertElementDoesntExist(sourceDocument, xPath);
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, xPath);
		assertElementsType(sourceDocument, ArrayList.class, xPath);
	}

	@Test
	public void shouldMoveUserActionsIntoDefaultProjectsActionsList() throws Exception {
		final String xPath = "/ontrackXML/userActions/*";
		final List<Element> originalActions = getElements(sourceDocument, xPath);

		migration.apply(sourceDocument);

		final List<Element> movedActions = getElements(sourceDocument, "/ontrackXML/projects/project/actions/*");

		assertEquals(originalActions.size(), movedActions.size());
		for (int i = 0; i < movedActions.size(); i++) {
			final Element originalAction = originalActions.get(i);
			final Element movedAction = movedActions.get(i);

			assertEquals(originalAction.attributeValue("timestamp"), movedAction.attributeValue("timestamp"));
			assertEquals(originalAction.selectObject("./action"), movedAction.selectObject("./action"));
		}

		assertElementDoesntExist(sourceDocument, xPath);
		assertElementsType(sourceDocument, ArrayList.class, xPath);
	}

	@Test
	public void shouldRemoveIdAttributeFromUserActions() throws Exception {
		assertAttributeExists(sourceDocument, "//userAction/@id");

		migration.apply(sourceDocument);

		assertAttributeDoesntExist(sourceDocument, "//userAction/@id");
		assertElementExists(sourceDocument, "//userAction");
	}

}
