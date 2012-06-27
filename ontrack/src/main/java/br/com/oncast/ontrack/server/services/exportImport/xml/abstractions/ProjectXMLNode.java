package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

@Root(name = "project")
public class ProjectXMLNode {

	@Element
	private ProjectRepresentation projectRepresentation;

	@ElementList
	private List<UserAction> actions;

	@SuppressWarnings("unused")
	// IMPORTANT The Simple Framework needs a default constructor for instantiate classes.
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
