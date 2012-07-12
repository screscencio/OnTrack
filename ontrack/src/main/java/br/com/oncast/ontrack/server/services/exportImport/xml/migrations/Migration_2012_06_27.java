package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

/**
 * Changes:
 * <ul>
 * <li>Updates projectId from long to UUID
 * </ul>
 * 
 */
public class Migration_2012_06_27 extends Migration {

	private List<Element> authorizations;
	private List<Element> projects;

	@Override
	protected void execute() throws Exception {
		projects = getElements("//project");
		authorizations = getElements("//projectAuthorization");

		updateProjectId();
	}

	private void updateProjectId() {
		for (final Element project : projects) {
			final String newProjectId = new UUID().toStringRepresentation();
			final Attribute oldProjectId = project.attribute("id");
			project.remove(oldProjectId);
			project.element("projectRepresentation").addElement(oldProjectId.getName())
					.addAttribute("id", newProjectId);

			updateAuthorizations(oldProjectId.getValue(), newProjectId);
		}
	}

	private void updateAuthorizations(final String oldId, final String newId) {
		for (final Element authorization : authorizations) {
			final Attribute oldProjectId = authorization.attribute("projectId");

			if (oldProjectId != null && oldProjectId.getValue().equals(oldId)) {
				changeToUuidElement(authorization, oldProjectId, newId);
			}
		}
	}

	private void changeToUuidElement(final Element element, final Attribute attribute, final String newValue) {
		element.remove(attribute);
		element.addElement(attribute.getName())
				.addAttribute("id", newValue);
	}

}
