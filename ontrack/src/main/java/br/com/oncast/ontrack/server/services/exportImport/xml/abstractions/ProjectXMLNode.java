package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "project")
public class ProjectXMLNode {

	@Element
	private ProjectRepresentation projectRepresentation;

	@ElementList
	private List<UserAction> actions;

	@SuppressWarnings("unused")
	private ProjectXMLNode() {}

	public ProjectXMLNode(final ProjectRepresentation projectRepresentation, final List<UserAction> actions) {
		this.projectRepresentation = projectRepresentation;
		this.actions = actions;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}

	public List<UserAction> getActions() {
		return actions;
	}

}
