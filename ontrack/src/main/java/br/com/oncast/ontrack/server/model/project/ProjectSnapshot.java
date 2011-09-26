package br.com.oncast.ontrack.server.model.project;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.utils.serializer.Serializer;
import br.com.oncast.ontrack.shared.model.project.Project;

@Entity
public class ProjectSnapshot {

	@Id
	@GeneratedValue
	private long id;

	@Lob
	private byte[] serializedProject;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	public ProjectSnapshot() {}

	public ProjectSnapshot(final Project project, final Date timestamp) throws IOException {
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

}
