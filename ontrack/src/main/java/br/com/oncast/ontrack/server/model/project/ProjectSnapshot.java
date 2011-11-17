package br.com.oncast.ontrack.server.model.project;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.utils.serializer.Serializer;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

@Entity
public class ProjectSnapshot {

	@Id
	@GeneratedValue
	private long id;

	@Lob
	private byte[] serializedProject;

	private long lastAppliedActionId;

	@OneToOne
	private ProjectRepresentation projectRepresentation;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	public ProjectSnapshot() {}

	public ProjectSnapshot(final Project project, final Date timestamp) throws IOException {
		projectRepresentation = project.getProjectRepresentation();
		setProject(project);
		setTimestamp(timestamp);
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public byte[] getSerializedProject() {
		return serializedProject;
	}

	public void setSerializedProject(final byte[] serializedProject) {
		this.serializedProject = serializedProject;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Project getProject() throws IOException, ClassNotFoundException {
		return (Project) Serializer.deserialize(serializedProject);
	}

	public void setProject(final Project project) throws IOException {
		this.serializedProject = Serializer.serialize(project);
	}

	public long getLastAppliedActionId() {
		return lastAppliedActionId;
	}

	public void setLastAppliedActionId(final long lastAppliedActionId) {
		this.lastAppliedActionId = lastAppliedActionId;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}

}
