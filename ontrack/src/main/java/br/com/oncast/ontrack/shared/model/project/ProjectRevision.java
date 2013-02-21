package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

public class ProjectRevision implements Serializable {

	private static final long serialVersionUID = 1L;

	private Project project;
	private long projectRevision;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectRevision() {}

	public ProjectRevision(final Project project, final long lastAppliedActionId) {
		this.project = project;
		this.projectRevision = lastAppliedActionId;
	}

	public Project getProject() {
		return project;
	}

	public long getRevision() {
		return projectRevision;
	}
}
