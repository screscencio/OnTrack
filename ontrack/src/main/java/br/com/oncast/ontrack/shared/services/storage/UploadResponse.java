package br.com.oncast.ontrack.shared.services.storage;

public interface UploadResponse {

	String getMessage();

	void setMessage(String message);

	String getStatus();

	void setStatus(String message);

	String getFileId();

	void setFileId(String fileId);
}
