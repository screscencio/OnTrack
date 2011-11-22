package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.util.List;

import org.dom4j.Element;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;

public class Migration_2011_11_18 extends Migration {

	@Override
	public void execute() throws Exception {
		mergePasswordsIntoTheirRelatedUser();
		final Element defaultProject = createDefaultProject();
		moveActionsToProjectAndRemoveActionId(defaultProject);
	}

	@SuppressWarnings("unchecked")
	private void mergePasswordsIntoTheirRelatedUser() {
		final List<Element> users = getElementsOfType(User.class);

		for (final Element user : users) {
			final String userId = user.attributeValue("id");
			removeIdAttributeFrom(user);

			final List<Element> passwordsForThisUser = getDocument().selectNodes("/ontrackXML/passwords/*[@userId='" + userId + "']");
			if (passwordsForThisUser.isEmpty()) continue;

			// Ignore if there are more than one password for the same user.
			user.addAttribute("passwordHash", passwordsForThisUser.get(0).attributeValue("passwordHash"));
			user.addAttribute("passwordSalt", passwordsForThisUser.get(0).attributeValue("passwordSalt"));

		}

		removePasswords();
	}

	private void removePasswords() {
		final Element passwords = (Element) getDocument().selectObject("/ontrackXML/passwords");
		getRootElement().remove(passwords);
	}

	private Element createDefaultProject() {
		final Element projects = addListElementTo(getRootElement(), "projects");
		final Element project = addElementWithName(projects, ProjectXMLNode.class);
		final Element representation = addElementWithName(project, ProjectRepresentation.class);
		representation.addAttribute("name", "Project");
		return project;
	}

	private void moveActionsToProjectAndRemoveActionId(final Element defaultProjectElement) {
		final Element actions = addListElementTo(defaultProjectElement, "actions");
		final List<Element> userActions = getElementsOfType(UserAction.class);
		for (final Element userAction : userActions) {
			userAction.detach();
			removeIdAttributeFrom(userAction);
			actions.add(userAction);
		}
		getRootElement().remove((Element) getDocument().selectObject("/ontrackXML/userActions"));
	}

	private void removeIdAttributeFrom(final Element element) {
		element.remove(element.attribute("id"));
	}

}
