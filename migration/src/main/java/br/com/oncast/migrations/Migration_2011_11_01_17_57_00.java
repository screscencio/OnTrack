package br.com.oncast.migrations;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import br.com.oncast.migration.Migration;

public class Migration_2011_11_01_17_57_00 extends Migration{

	@Override
	public void execute(Document document) throws Exception {
		Element root = document.getRootElement();
		
		addDefaultUser(root);
		
		addList(root, "passwords");
		
		List actions = document.selectNodes("//action[class'br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction']");
		Iterator it = actions.iterator();
		while(it.hasNext()){
			Element action = (Element) it.next();
			action.addAttribute("timestamp", new Date().toString());
			
		}
	}

	private void addDefaultUser(Element root) {
		Element users = addList(root, "users");

		Element defaultUser = users.addElement("user");
		defaultUser.addAttribute("id", "1");
		defaultUser.addAttribute("email", "admin@ontrack.com.br");
	}

}
