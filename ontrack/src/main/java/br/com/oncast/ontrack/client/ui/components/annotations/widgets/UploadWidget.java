package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.storage.FileUploadFormObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UploadWidget extends Composite {

	private static UploadWidgetUiBinder uiBinder = GWT.create(UploadWidgetUiBinder.class);

	interface UploadWidgetUiBinder extends UiBinder<Widget, UploadWidget> {}

	interface UploadWidgetStyle extends CssResource {
		String removeImg();
	}

	@UiField
	protected UploadWidgetStyle style;

	@UiField
	protected FocusPanel uploadIcon;

	@UiField
	protected FileUpload fileUpload;

	@UiField
	protected Label fileNameLabel;

	@UiField
	protected TextBox fileName;

	@UiField
	protected TextBox projectId;

	@UiField
	protected FormPanel form;

	private ActionExecutionListener actionExecutionListener;

	public UploadWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		fileName.setName(FileUploadFormObject.FieldNames.FILE_NAME);
		fileUpload.setName(FileUploadFormObject.FieldNames.FILE);
		projectId.setName(FileUploadFormObject.FieldNames.PROJECT_ID);

		fileUpload.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(final ChangeEvent event) {
				final String name = getFilename();
				fileNameLabel.setText(name);
				setUploadFieldVisible(!name.isEmpty());
			}
		});
	}

	public void setActionUrl(final String url) {
		form.setAction(URL.encode(url));
	}

	@Override
	protected void onLoad() {
		setUploadFieldVisible(false);
	}

	public void setUploadFieldVisible(final boolean b) {
		fileNameLabel.setVisible(b);
		uploadIcon.setStyleName(style.removeImg(), b);
	}

	private static native void clickOnInputFile(Element elem) /*-{
		elem.click();
	}-*/;

	@UiHandler("uploadIcon")
	protected void onUploadIconClicked(final ClickEvent e) {
		if (!isUploadWidgetVisible()) clickOnInputFile(fileUpload.getElement());
		else setUploadFieldVisible(false);
	}

	private boolean isUploadWidgetVisible() {
		return fileNameLabel.isVisible();
	}

	public String getFilename() {
		final String name = fileUpload.getFilename();
		return name.substring(name.lastIndexOf("\\") + 1);
	}

	public void submitForm(final UploadWidgetListener listener) {
		final String filename = getFilename();
		if (!isUploadWidgetVisible() || filename.isEmpty()) {
			listener.onUploadCompleted(null);
			return;
		}

		projectId.setValue(getCurrentProject().getId().toStringRepresentation());
		fileName.setValue(filename);
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener(filename, listener));
		form.submit();
	}

	private ProjectRepresentation getCurrentProject() {
		return ClientServiceProvider.getInstance().getProjectRepresentationProvider().getCurrent();
	}

	private ActionExecutionListener getActionExecutionListener(final String filename, final UploadWidgetListener listener) {
		return actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof FileUploadAction) {
					final FileUploadAction uploadAction = (FileUploadAction) action;
					if (filename.equals(uploadAction.getFileName())) {
						getActionExecutionService().removeActionExecutionListener(actionExecutionListener);
						listener.onUploadCompleted(uploadAction.getReferenceId());
					}
				}
			}

		};
	}

	private ActionExecutionService getActionExecutionService() {
		return ClientServiceProvider.getInstance().getActionExecutionService();
	}

	public interface UploadWidgetListener {

		void onUploadCompleted(UUID referenceId);

	}

}
