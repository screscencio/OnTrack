package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.util.List;

import org.dom4j.Element;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class Migration_2011_11_18 extends Migration {

	@Override
	public void execute() throws Exception {
		removeUserActionIdAttribute();
		createDefaultProject();
	}

	private void createDefaultProject() {
		final Element projects = addListElementTo(getRootElement(), "projects");
		final Element project = addElementWithName(projects, ProjectXMLNode.class);
		final Element representation = addElementWithName(project, ProjectRepresentation.class);
		representation.addAttribute("name", "Project");
		final Element actions = addListElementTo(project, "actions");
	}

	private void removeUserActionIdAttribute() {
		// final List userActions = getAllElementsOfType(UserAction.class);
		final List<Element> userActions = getElementsOfType(UserAction.class);
		for (final Element userAction : userActions) {
			userAction.remove(userAction.attribute("id"));
		}
	}

}
