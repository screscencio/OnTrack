package br.com.oncast.ontrack.server.services.api.bean;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionExecutionApiRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElementWrapper(name = "actionList")
	@XmlAnyElement
	private List<ModelAction> actionList;

	private UUID projectId;

	ActionExecutionApiRequest() {}

	public ActionExecutionApiRequest(final UUID projectId, final List<ModelAction> actionList) {
		this.projectId = projectId;
		this.actionList = actionList;
	}

	public List<ModelAction> getActionList() {
		return actionList;
	}

	public UUID getProjectId() {
		return projectId;
	}

}
