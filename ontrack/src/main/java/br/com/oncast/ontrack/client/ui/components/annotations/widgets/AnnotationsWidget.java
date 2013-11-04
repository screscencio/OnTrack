package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.UploadWidget.UploadFileChangeListener;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.UploadWidget.UploadWidgetListener;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationsWidget extends Composite {

	private static final AnnotationsWidgetMessages MESSAGES = GWT.create(AnnotationsWidgetMessages.class);

	private static AnnotationsWidgetUiBinder uiBinder = GWT.create(AnnotationsWidgetUiBinder.class);

	interface AnnotationsWidgetUiBinder extends UiBinder<Widget, AnnotationsWidget> {}

	interface AnnotationsWidgetStyle extends CssResource {
		String createImpedimentButton();
	}

	@UiField
	AnnotationsWidgetStyle style;

	@UiField
	FocusPanel root;

	@UiField
	protected RichTextArea newAnnotationText;

	@UiField
	protected UploadWidget uploadWidget;

	@UiField
	protected Panel newAnnotationContainer;

	@UiField
	protected Button createButton;

	@UiField
	protected Element createButtonIcon;

	@UiField
	protected SpanElement createButtonText;

	@UiField
	protected UserWidget author;

	@UiFactory
	protected UserWidget createUserWidget() {
		return new UserWidget(ClientServices.getCurrentUser());
	}

	@UiField
	protected ModelWidgetContainer<Annotation, AnnotationTopic> annotationsWidgetContainer;

	@UiFactory
	protected ModelWidgetContainer<Annotation, AnnotationTopic> createAnnotationsContainer() {
		return new ModelWidgetContainer<Annotation, AnnotationTopic>(new ModelWidgetFactory<Annotation, AnnotationTopic>() {
			@Override
			public AnnotationTopic createWidget(final Annotation modelBean) {
				return new AnnotationTopic(subjectId, modelBean);
			}
		});
	}

	private final UUID subjectId;

	private ActionExecutionListener actionsListener;

	private AnnotationType selectedType = AnnotationType.SIMPLE;

	public AnnotationsWidget(final UUID subjectId) {
		this.subjectId = subjectId;
		initWidget(uiBinder.createAndBindUi(this));
		newAnnotationContainer.setVisible(false);
		uploadWidget.setActionUrl("/application/file/upload");
		uploadWidget.registerUploadFileChangeListener(new UploadFileChangeListener() {
			@Override
			public void onFileChange() {
				updateAnnotationCreateButton();
			}

		});
		createButton.setEnabled(false);
	}

	@Override
	protected void onLoad() {
		update();

		getActionExecutionService().addActionExecutionListener(getListener());
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(actionsListener);
	}

	@UiHandler("createButton")
	protected void onCreateClick(final ClickEvent event) {
		addAnnotation();
	}

	@UiHandler("cancelButton")
	protected void onCancelClick(final ClickEvent event) {
		cancelCreation();
	}

	@UiHandler("newAnnotationText")
	protected void onNewAnnotationTextKeyDown(final KeyDownEvent e) {
		if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) cancelCreation();
		if (!new Shortcut(BrowserKeyCodes.KEY_ENTER).with(ControlModifier.PRESSED).accepts(e.getNativeEvent())) return;

		e.preventDefault();
		e.stopPropagation();

		addAnnotation();
	}

	@UiHandler("newAnnotationText")
	protected void onNewAnnotationTextKeyUp(final KeyUpEvent e) {
		updateAnnotationCreateButton();
	}

	private void updateAnnotationCreateButton() {
		createButton.setEnabled(uploadWidget.hasChosenUploadFile() || !isNewAnnotationTextEmpty());
	}

	private boolean isNewAnnotationTextEmpty() {
		return newAnnotationText.getText().trim().isEmpty();
	}

	private void cancelCreation() {
		hideNewAnnotationContainer();
	}

	public void enterEditMode(final AnnotationType annotationType) {
		setAnnotationType(annotationType);
		newAnnotationContainer.setVisible(true);
		setFocus(true);
	}

	private void setAnnotationType(final AnnotationType type) {
		if (this.selectedType == type) return;

		this.selectedType = type;
		updateAnnotationType();
	}

	public void setFocus(final boolean b) {
		newAnnotationText.setFocus(b);
	}

	public int getWidgetCount() {
		return annotationsWidgetContainer.getWidgetCount();
	}

	private void addAnnotation() {
		final String fileName = uploadWidget.getFilename();
		if (isNewAnnotationTextEmpty() && fileName.isEmpty()) return;

		uploadWidget.submitForm(new UploadWidgetListener() {
			@Override
			public void onUploadCompleted(final UUID fileRepresentationId) {
				getProvider().details().createAnnotationFor(subjectId, selectedType, newAnnotationText.getHTML().trim(), fileRepresentationId);
				hideNewAnnotationContainer();
			}

		});
	}

	private void hideNewAnnotationContainer() {
		uploadWidget.clearChosenFile();
		newAnnotationText.setText("");
		newAnnotationContainer.setVisible(false);
		createButton.setEnabled(false);
		root.setFocus(true);
	}

	private void update() {
		annotationsWidgetContainer.update(getAnnotationService().getAnnotationsFor(subjectId));
	}

	private void updateAnnotationType() {
		String buttonText;
		String buttonIconStyle;

		if (selectedType == AnnotationType.OPEN_IMPEDIMENT) {
			buttonText = MESSAGES.createImpediment();
			buttonIconStyle = "icon-flag";
			createButton.addStyleName(style.createImpedimentButton());
		} else {
			buttonText = MESSAGES.createAnnotation();
			buttonIconStyle = "icon-comment";
			createButton.removeStyleName(style.createImpedimentButton());
		}
		createButtonText.setInnerText(buttonText);
		createButtonIcon.setClassName(buttonIconStyle);
	}

	private ActionExecutionListener getListener() {
		if (actionsListener != null) return actionsListener;

		return actionsListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final ActionExecutionContext executionContext,
					final boolean isUserAction) {
				if (action instanceof AnnotationAction && action.getReferenceId().equals(subjectId)) update();
				if (action instanceof ImpedimentAction && action.getReferenceId().equals(subjectId)) update();
			}
		};
	}

	private DetailService getAnnotationService() {
		return getProvider().details();
	}

	private ActionExecutionService getActionExecutionService() {
		return getProvider().actionExecution();
	}

	private ClientServices getProvider() {
		return ClientServices.get();
	}

}
