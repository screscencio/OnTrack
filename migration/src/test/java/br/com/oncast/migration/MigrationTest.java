package br.com.oncast.migration;

import static org.junit.Assert.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.migration.test.migrations.Migration_2011_10_01_14_32_11;
import br.com.oncast.migration.test.migrations2.Migration2_2011_08_01_14_32_11;
import br.com.oncast.migration.test.migrations2.Migration2_2011_10_01_14_32_11;
import br.com.oncast.migration.test.migrations2.subPackage.Migration2_2011_11_10_08_15_39;

public class MigrationTest {
	
	private static final String testXML = 
			"<?xml version=\"1.0\" ?>" +
			"<ontrackXML version=\"1320175697999\">" +
			"	<users class=\"java.util.ArrayList\">" +
			"		<user id=\"1\" email=\"user1@email\"/>" +
			"		<user id=\"2\" email=\"user2@email\"/>" +
			"	</users>\n" +
			"		<passwords class=\"java.util.ArrayList\">" +
			"		<password id=\"0\" userId=\"1\" passwordHash=\"B2mo6p1KwIpcm_LakZWdWINAkXk=\" passwordSalt=\"58d2kj1EAPc=\"/>" +
			"		<password id=\"0\" userId=\"2\" passwordHash=\"jWZP0fb2I$SX7j8nRFu7eVn28hw=\" passwordSalt=\"kYnCwQfWCZY=\"/>" +
			"	</passwords>" + 
			"	<userActions class=\"java.util.ArrayList\">" +
			"		<userAction id=\"1\" timestamp=\"2011-11-01 17:28:17.999 BRST\">" +
			"			<action class=\"br.com.oncast.ontrack.shared.model.actions.ReleaseRemoveAction\">" +
            "				<referenceId id=\"3A63F0EF-D5E9-4C9C-9AFC-010033D36BF6\"/>" +
         	"			</action>"+
         	"		</userAction>" +
         	"		<userAction id=\"1\" timestamp=\"2011-11-01 17:28:17.999 BRST\">" +
         	"			<action class=\"br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction\" newDescription=\"descricao\">" +
         	"				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
         	"   			<subActionList class=\"java.util.ArrayList\">" +
         	"      				<modelAction class=\"br.com.oncast.ontrack.shared.model.actions.ScopeBindReleaseAction\" newReleaseDescription=\"release\" scopePriority=\"-1\">" +
         	"         				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
         	"      				</modelAction>" +
         	"      				<modelAction class=\"br.com.oncast.ontrack.shared.model.actions.ScopeDeclareEffortAction\" hasDeclaredEffort=\"true\" newDeclaredEffort=\"3\">" +
         	"         				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
         	"      				</modelAction>" +
         	"      				<modelAction class=\"br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction\" newProgressDescription=\"d\">" +
         	"         				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
         	"      				</modelAction>" +
         	"   			</subActionList>" +
         	"			</action>" +
         	"		</userAction>" +
			"	</userActions>" +
			"</ontrackXML>"; 

	@Test
	public void allInstancesOfSameMigrationIsEqual() {
		assertEquals(new Migration_2011_10_01_14_32_11(), new Migration_2011_10_01_14_32_11());
		assertEquals(new Migration2_2011_08_01_14_32_11(), new Migration2_2011_08_01_14_32_11());
		assertFalse(new Migration2_2011_08_01_14_32_11().equals(new Migration_2011_10_01_14_32_11()));
	}
	
	@Test
	public void migrationDateShouldBeTheDateOnHisName() throws Exception {
		assertEquals("2011_10_01_14_32_11", new Migration_2011_10_01_14_32_11().getDateString());
		assertEquals("2011_08_01_14_32_11", new Migration2_2011_08_01_14_32_11().getDateString());
	}
	
	@Test
	public void migrationsIsOrderedByTheDateOnHisName() throws Exception {
		assertTrue(new Migration2_2011_08_01_14_32_11().compareTo(new Migration_2011_10_01_14_32_11()) < 0);
		assertTrue(new Migration2_2011_11_10_08_15_39().compareTo(new Migration_2011_10_01_14_32_11()) > 0);
		assertTrue(new Migration2_2011_10_01_14_32_11().compareTo(new Migration_2011_10_01_14_32_11()) == 0);
	}
	
	@Test
	public void migrationShouldNotBeExecutedIfDocumentVersionIsGreaterThanMigrationVersion() throws Exception {
		String migrationVersion = "1320175697998";
		String documentVersion = "1320175697999";
		assertTrue(documentVersion.compareTo(migrationVersion) > 0);
		
		Document document = DocumentHelper.parseText(testXML);
		Migration spy = Mockito.spy(new Migration2_2011_08_01_14_32_11());
		Mockito.when(spy.getDateString()).thenReturn(migrationVersion);
		
		spy.apply(document);
		
		Mockito.verify(spy, Mockito.never()).execute(document);
	}

}
