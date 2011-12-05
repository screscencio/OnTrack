package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class Migration_2011_12_01 extends Migration {

	private int lastUserId = 0;
	private int lastProjectId = 0;

	@Override
	public void execute() throws Exception {
		addIdToEachUser();
		addIdToEachProject();
		createProjectAuthorizations();
	}

	private void addIdToEachUser() {
		for (final Element user : getElements("/ontrackXML/users/user")) {
			user.addAttribute("id", "" + ++lastUserId);
		}
	}

	private void addIdToEachProject() {
		for (final Element project : getElements("/ontrackXML/projects/project")) {
			project.addAttribute("id", "" + ++lastProjectId);
		}
	}

	private void createProjectAuthorizations() {
		final Element list = addListElementTo(getRootElement(), "projectAuthorizations");
		for (int projectId = 1; projectId <= lastProjectId; projectId++) {
			for (int userId = 1; userId <= lastUserId; userId++) {
				final Element auth = list.addElement("projectAuthorization");
				auth.addAttribute("projectId", "" + projectId);
				auth.addAttribute("userId", "" + userId);
			}
		}
	}
}
