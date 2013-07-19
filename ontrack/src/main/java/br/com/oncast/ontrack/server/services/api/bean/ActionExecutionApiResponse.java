package br.com.oncast.ontrack.server.services.api.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionExecutionApiResponse extends BaseApiResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private long projectRevision = -1;

	ActionExecutionApiResponse() {}

	public ActionExecutionApiResponse(final long projectRevision) {
		this.projectRevision = projectRevision;
	}

	public ActionExecutionApiResponse(final Exception exception) {
		super(exception);
	}

	public boolean hasSucceeded() {
		return this.projectRevision > 0 && !hasErrors();
	}

	public long getProjectRevision() {
		return projectRevision;
	}

}
