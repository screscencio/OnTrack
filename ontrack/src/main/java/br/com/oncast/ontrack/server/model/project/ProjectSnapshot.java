package br.com.oncast.ontrack.server.model.project;

import br.com.oncast.ontrack.server.utils.serializer.Serializer;
import br.com.oncast.ontrack.shared.model.project.Project;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ProjectSnapshot {

	@Id
	private String id;

	@Lob
	private byte[] serializedProject;

	private long lastAppliedActionId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	public ProjectSnapshot() {}

	public ProjectSnapshot(final Project project, final Date timestamp) throws IOException {
		this.id = project.getProjectRepresentation().getId().toString();
		setProject(project);
		setTimestamp(timestamp);
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
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
}
