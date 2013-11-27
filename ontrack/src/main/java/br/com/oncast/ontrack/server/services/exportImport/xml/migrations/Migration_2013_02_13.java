package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.tree.DOMTreeHelper;

/**
 * Changes:
 * <ul>
 * <li>Adds human id counter attribute to ProjectRepresentation
 * <li>Adds ScopeBindHumanIdAction after ScopeBindReleaseActions
 * </ul>
 * 
 */
public class Migration_2013_02_13 extends Migration {

	private static final String SCOPE_BIND_RELEASE_ACTION = "br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction";
	private static final String SCOPE_BIND_HUMAN_ID_ACTION = "br.com.oncast.ontrack.shared.model.action.ScopeBindHumanIdAction";

	private static final String ID = "id";
	private int idCounter;

	@Override
	@SuppressWarnings("unchecked")
	protected void execute() throws Exception {
		idCounter = 0;
		final List<String> processedScopes = new ArrayList<String>();

		for (final Element project : getElements("//project")) {
			final List<Element> actionsList = project.selectNodes("//*[@class='" + SCOPE_BIND_RELEASE_ACTION + "']");
			for (final Element action : actionsList) {
				final String newReleaseDescription = action.attributeValue("newReleaseDescription");
				final String scopeId = getReferenceId(action);
				if (newReleaseDescription == null || newReleaseDescription.isEmpty() || processedScopes.contains(scopeId)) continue;

				final Element userAction = getUserAction(action);
				final Element scopeBindHumanIdAction = createScopeBindHumanAction(userAction, scopeId);

				DOMTreeHelper.insertAfter(userAction, scopeBindHumanIdAction);
				processedScopes.add(scopeId);
			}

			project.element("projectRepresentation").addAttribute("humanIdCounter", "" + idCounter);
		}

	}

	private Element createScopeBindHumanAction(final Element userAction, final String scopeId) {
		final Element humanIdAction = userAction.createCopy();
		humanIdAction.remove(humanIdAction.element("action"));
		humanIdAction.addElement("action")
				.addAttribute("class", SCOPE_BIND_HUMAN_ID_ACTION)
				.addAttribute("humanId", ++idCounter + "")
				.addElement("scopeId")
				.addAttribute(ID, scopeId)
				.getParent().addElement("metadataId")
				.addAttribute(ID, new UUID().toString());

		return humanIdAction;
	}

	private Element getUserAction(final Element action) {
		Element userAction = action.getParent();
		while (!userAction.isRootElement() && !userAction.getName().equals("userAction"))
			userAction = userAction.getParent();
		return userAction;
	}

	private String getReferenceId(final Element action) {
		return action.element("referenceId").attributeValue(ID);
	}

}
