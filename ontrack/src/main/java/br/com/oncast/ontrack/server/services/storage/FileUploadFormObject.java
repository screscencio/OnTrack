package br.com.oncast.ontrack.server.services.storage;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.fileupload.FileItem;

import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.storage.FileUploadFieldNames;

import com.google.common.io.Files;

public class FileUploadFormObject {

	private byte[] data;
	private String fileName;
	private UUID projectId;

	public FileUploadFormObject() {}

	public void parseField(final FileItem fileItem) {
		if (!fileItem.isFormField() && FileUploadFieldNames.FILE.equals(fileItem.getFieldName())) {
			this.data = fileItem.get();
		}
		else if (FileUploadFieldNames.FILE_NAME.equals(fileItem.getFieldName())) {
			try {
				// FIXME Lobo review if this is tolerable to correct encoding problems with filename with accents
				this.fileName = URLDecoder.decode(fileItem.getString(), "UTF-8");
			}
			catch (final UnsupportedEncodingException e) {
				this.fileName = fileItem.getString();
			}
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