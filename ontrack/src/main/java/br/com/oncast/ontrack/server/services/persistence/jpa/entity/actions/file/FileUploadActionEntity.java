package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.file;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;

@Entity(name = "FileUpload")
@ConvertTo(FileUploadAction.class)
public class FileUploadActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String fileRepresentationId;

	@Column(name = ActionTableColumns.STRING_2)
	private String fileName;

	@Column(name = ActionTableColumns.STRING_3)
	private String filePath;

	public String getFileRepresentationId() {
		return fileRepresentationId;
	}

	public void setFileRepresentationId(final String fileRepresentationId) {
		this.fileRepresentationId = fileRepresentationId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
