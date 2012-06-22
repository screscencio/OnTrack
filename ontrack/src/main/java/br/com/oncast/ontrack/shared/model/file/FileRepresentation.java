package br.com.oncast.ontrack.shared.model.file;

import java.io.Serializable;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.file.FileRepresentationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(FileRepresentationEntity.class)
public class FileRepresentation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private String filePath;
	private UUID id;
	private UUID projectId;

	public FileRepresentation() {}

	public FileRepresentation(final String fileName, final String filePath, final UUID projectId) {
		this(new UUID(), fileName, filePath, projectId);
	}

	public FileRepresentation(final UUID id, final String fileName, final String filePath, final UUID projectId) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.projectId = projectId;
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final FileRepresentation other = (FileRepresentation) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	public UUID getId() {
		return this.id;
	}

	public FileRepresentation setProjectId(final UUID projectId) {
		this.projectId = projectId;
		return this;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileName() {
		return fileName;
	}

}
