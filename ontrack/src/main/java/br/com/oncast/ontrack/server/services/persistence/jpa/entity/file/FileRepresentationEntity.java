package br.com.oncast.ontrack.server.services.persistence.jpa.entity.file;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@ConvertTo(FileRepresentation.class)
public class FileRepresentationEntity {

	@Column(name = "fileName")
	private String fileName;

	@Column(name = "filePath")
	private String filePath;

	@Id
	@ConvertUsing(StringToUuidConverter.class)
	private String id;

	@Column(name = "projectId")
	@ConvertUsing(StringToUuidConverter.class)
	private String projectId;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(final String projectId) {
		this.projectId = projectId;
	}

}
