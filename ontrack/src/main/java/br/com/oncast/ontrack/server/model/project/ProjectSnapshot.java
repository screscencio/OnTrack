package br.com.oncast.ontrack.server.model.project;

import java.util.Date;

import br.com.oncast.ontrack.shared.model.project.Project;

public class ProjectSnapshot {

	private final Project project;
	private final Date timestamp;

	public ProjectSnapshot(final Project project, final Date timestamp) {
		this.project = project;
		this.timestamp = timestamp;
	}

	public Project getProject() {
		return project;
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
