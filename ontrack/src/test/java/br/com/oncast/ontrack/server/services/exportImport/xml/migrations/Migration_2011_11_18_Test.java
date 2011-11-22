package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertAttributeDoesntExist;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertAttributeExists;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementAttributeValueIs;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementDoesntExist;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementExists;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementHasTheseAttributesAndNothingElse;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementsType;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.readXMLFromFile;

import java.util.ArrayList;

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
	public void shouldRemoveIdAttributeFromUserActions() throws Exception {
		assertAttributeExists(sourceDocument, "//userAction/@id");
		migration.apply(sourceDocument);

		assertAttributeDoesntExist(sourceDocument, "//userAction/@id");
		assertElementExists(sourceDocument, "//userAction");
	}

	@Test
	public void shouldCreateProjectsList() throws Exception {
		final String xPath = "//projects";
		assertElementDoesntExist(sourceDocument, xPath);
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, xPath);
		assertElementsType(sourceDocument, ArrayList.class, xPath);
	}

	@Test
	public void shouldCreateDefaultProjectInsideProjectsList() throws Exception {
		final String xPath = "//projects/project";
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

	public void shouldCreateActionsListAsAChildElementOfTheDefaultProject() throws Exception {
		final String xPath = "//project/actions";
		assertElementDoesntExist(sourceDocument, xPath);
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, xPath);
		assertElementsType(sourceDocument, ArrayList.class, xPath);
	}

	@Test
	// FIXME stoped Here
	public void shouldMoveUserActionsIntoDefaultProjectsActionsList() throws Exception {
		final String xPath = "//project/actions";
		assertElementDoesntExist(sourceDocument, xPath);
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, xPath);
		assertElementsType(sourceDocument, ArrayList.class, xPath);
	}

}
