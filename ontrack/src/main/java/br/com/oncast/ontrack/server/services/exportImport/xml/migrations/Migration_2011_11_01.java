package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction;

public class Migration_2011_11_01 extends Migration {

	@Override
	public void execute() throws Exception {
		addDefaultUser();
		addDefaultPassword();
		addTimeStampForAllDeclareProgressActions();
	}

	private void addDefaultUser() {
		final Element users = addListElementTo(getRootElement(), "users");

		final Element defaultUser = users.addElement("user");
		defaultUser.addAttribute("id", "1");
		defaultUser.addAttribute("email", "admin@ontrack.com");
	}

	private void addDefaultPassword() {
		final Element passwords = addListElementTo(getRootElement(), "passwords");

		final Element password = passwords.addElement("password");
		password.addAttribute("id", "1");
		password.addAttribute("userId", "1");
		password.addAttribute("passwordHash", "FfWz98wvWdj1OvOFWIO1dNVXJC0=");
		password.addAttribute("passwordSalt", "Q7wT3NyAloU=");
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

}
