package br.com.oncast.ontrack.shared.messageCode;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface UploadMessages extends BaseMessages {

	@Description("Message returned from server when the user sends a file larger than the permitted size.")
	@DefaultMessage("The uploaded file exceeds the maximun size ({0})")
	String fileSizeLimit(String sizeLimit);

	@Description("Message returned from server when the upload is complete.")
	@DefaultMessage("Upload complete")
	String serverUploadComplete();

	@Description("Notifies the user that the he needs to wait the upload.")
	@DefaultMessage("Uploading your file...")
	String uploading();

	@Description("Notifies the user that his upload has completed.")
	@DefaultMessage("Upload Completed!")
	String uploadCompleted();

}
