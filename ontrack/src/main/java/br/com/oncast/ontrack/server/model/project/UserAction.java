package br.com.oncast.ontrack.server.model.project;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

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

	@Element
	@IgnoredByDeepEquality
	private UUID userId;

	@Element
	@IgnoredByDeepEquality
	private UUID uniqueId;

	public UserAction() {}

	public UUID getUniqueId() {
		return uniqueId;
	}

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

	public UUID getUserId() {
		return userId;
	}
}
