package br.com.oncast.ontrack.shared.model.file;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.file.FileRepresentationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;

//FIXME save the file size
@ConvertTo(FileRepresentationEntity.class)
public class FileRepresentation implements Serializable, HasUUID {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private String filePath;
	private UUID id;
	private UUID projectId;

	public FileRepresentation() {}

	public FileRepresentation(final UUID id, final String fileName, final String filePath, final UUID projectId) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.projectId = projectId;
		this.id = id;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	@Override
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
