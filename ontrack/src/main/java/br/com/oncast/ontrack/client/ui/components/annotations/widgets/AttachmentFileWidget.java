package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.services.storage.FileUploadFieldNames;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AttachmentFileWidget extends Composite {

	private static AttachmentFileWidgetUiBinder uiBinder = GWT.create(AttachmentFileWidgetUiBinder.class);

	interface AttachmentFileWidgetUiBinder extends UiBinder<Widget, AttachmentFileWidget> {}

	@UiField
	Anchor downloadLink;

	public AttachmentFileWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AttachmentFileWidget(final FileRepresentation fileRepresentation) {
		this();
		downloadLink.setTarget("_blank");
		downloadLink.setText(fileRepresentation.getFileName());
		downloadLink.setHref(URL.encode(GWT.getModuleBaseURL() + "file/download?" + FileUploadFieldNames.FILE_ID + "="
				+ fileRepresentation.getId().toStringRepresentation()));
	}
}
