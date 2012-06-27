package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import com.google.common.collect.HashMultimap;

/**
 * Changes:
 * <ul>
 * <li>Add TeamInviteAction to existing user
 * </ul>
 * 
 */
public class Migration_2012_03_15 extends Migration {

	private static final String TEM_INVITE_ACTION_CLASS = "br.com.oncast.ontrack.shared.model.action.TeamInviteAction";
	private final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S z");
	private List<Element> users;
	private List<Element> projects;

	@Override
	protected void execute() throws Exception {
		final Map<String, Collection<String>> map = mapAuthorizedUsersByProject();

		for (final Entry<String, Collection<String>> e : map.entrySet()) {

			final Element actionsList = getActionsListFor(e.getKey());
			for (final String userId : e.getValue()) {
				addTeamInviteAction(actionsList, userId);
			}
		}
	}

	private Map<String, Collection<String>> mapAuthorizedUsersByProject() {
		final List<Element> authorizations = getElements("//projectAuthorization");

		final HashMultimap<String, String> authorizationMap = HashMultimap.create();
		for (final Element authorization : authorizations) {
			authorizationMap.put(authorization.attributeValue("projectId"), authorization.attributeValue("userId"));
		}
		return authorizationMap.asMap();
	}

	private void addTeamInviteAction(final Element actions, final String userId) {
		actions.addElement("userAction")
				.addAttribute("timestamp", formater.format(new Date()))
				.addAttribute("userId", "1")

				.addElement("action")
				.addAttribute("class", TEM_INVITE_ACTION_CLASS)
				.addElement("inviteeEmail")
				.addAttribute("id", getEmailFor(userId));
	}

	private Element getActionsListFor(final String projectId) {
		for (final Element project : getProjects()) {
			if (project.attributeValue("id").equals(projectId)) return project.element("actions");
		}
		return null;
	}

	private String getEmailFor(final String userId) {
		for (final Element user : getUsers()) {
			if (user.attributeValue("id").equals(userId)) return user.attributeValue("email");
		}
		return "";
	}

	private List<Element> getProjects() {
		return projects == null ? projects = getElements("//project") : projects;
	}

	private List<Element> getUsers() {
		return users == null ? users = getElements("//user") : users;
	}

}
