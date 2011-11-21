package br.com.oncast.ontrack.server.model.project;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

public class UserAction {

	@IgnoredByDeepEquality
	private long id;

	@Element
	private ModelAction action;

	@Attribute
	@IgnoredByDeepEquality
	private Date timestamp;

	@IgnoredByDeepEquality
	private ProjectRepresentation projectRepresentation;

	public UserAction() {}

	public long getId() {
		return id;
	}

	public ModelAction getModelAction() {
		return action;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}
}
