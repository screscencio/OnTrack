package br.com.oncast.ontrack.server.services.storage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.fileupload.FileItem;

import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.storage.FileUploadFieldNames;

import com.google.common.io.Files;

public class FileUploadFormObject {

	private byte[] data;
	private String fileName;
	private UUID projectId;

	public void parseField(final FileItem fileItem) {
		if (!fileItem.isFormField() && FileUploadFieldNames.FILE.equals(fileItem.getFieldName())) {
			this.data = fileItem.get();
		}
		else if (FileUploadFieldNames.FILE_NAME.equals(fileItem.getFieldName())) {
			this.fileName = fileItem.getString();
		}
		else if (FileUploadFieldNames.PROJECT_ID.equals(fileItem.getFieldName())) {
			this.projectId = new UUID(fileItem.getString());
		}
	}

	public File getFile(final File baseDir) throws IOException {
		final File file = new File(baseDir, fileName);
		Files.write(data, file);
		return file;
	}

	public boolean isComplete() {
		return data != null && fileName != null & projectId != null;
	}

	public UUID getProjectId() {
		return projectId;
	}

}