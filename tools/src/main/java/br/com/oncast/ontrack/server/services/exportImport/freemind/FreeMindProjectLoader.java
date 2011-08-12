package br.com.oncast.ontrack.server.services.exportImport.freemind;

import java.io.File;

import br.com.oncast.ontrack.server.services.exportImport.freemind.abstractions.FreeMindMap;
import br.com.oncast.ontrack.server.services.exportImport.freemind.abstractions.Icon;
import br.com.oncast.ontrack.server.services.exportImport.freemind.abstractions.MindNode;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class FreeMindProjectLoader {
	private final FreeMindMap mm;
	private final Project project = new Project();

	private FreeMindProjectLoader(FreeMindMap mm) {
		this.mm = mm;
	}

	public static Project loadMap(File mmFile) {
		return new FreeMindProjectLoader(FreeMindMap.open(mmFile)).load();
	}

	private Project load() {
		MindNode mmRoot = mm.root();
		Scope root = new Scope(mmRoot.getText());
		project.setProjectScope(root);
		visitScope(root, mmRoot);

		return project;
	}

	private static void visitScope(Scope scope, MindNode node) {
		scope.setDescription(node.getText());
		for (MindNode child : node.getChildren()) {
			if (child.hasIcon(Icon.CALENDAR)) {
				scope.setRelease(new Release(child.getText()));
			}
			else if (child.hasIcon(Icon.LAUNCH) && !child.hasIcon(Icon.WIZARD)) {
				scope.getProgress().setDescription(child.getText());
			}
			else if (child.hasIcon(Icon.HOURGLASS) && !child.hasIcon(Icon.WIZARD)) {
				// TODO set progress
			}
			else {
				Scope s = new Scope(child.getText());
				visitScope(s,child);
				scope.add(s);
			}
		}
	}
}
