package br.com.oncast.ontrack.server.services.exportImport.xml.converters;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import br.com.oncast.migration.Migration;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction;

public class Migration_2011_11_01 extends Migration {

	@Override
	public void execute() throws Exception {
		addDefaultUser();

		addList(getRootElement(), "passwords");

		addTimeStampForAllDeclareProgressActions();
	}

	private void addTimeStampForAllDeclareProgressActions() {
		final List<Element> actions = getAllElementsOfType(ScopeDeclareProgressAction.class);
		final Iterator<Element> it = actions.iterator();
		while (it.hasNext()) {
			final Element action = it.next();
			final Element userAction = (Element) action.selectObject("ancestor::userAction");
			action.addAttribute("timestamp", userAction.attributeValue("timestamp"));

		}
	}

	private void addDefaultUser() {
		final Element users = addList(getRootElement(), "users");

		final Element defaultUser = users.addElement("user");
		defaultUser.addAttribute("id", "1");
		defaultUser.addAttribute("email", "admin@ontrack.com");
	}

}
