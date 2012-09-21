package br.com.oncast.ontrack.shared.services.storage;

import br.com.oncast.ontrack.shared.messageCode.UploadMessageCode;

public interface UploadResponse {

	UploadMessageCode getMessage();

	void setMessage(UploadMessageCode code);

	String getMaxSize();

	void setMaxSize(String maxSize);

	String getStatus();

	void setStatus(String status);

	String getFileId();

	void setFileId(String fileId);
}
