package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.annotations.AnnotationService;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.UploadWidget.UploadWidgetListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationsWidget extends Composite {

	private static AnnotationsWidgetUiBinder uiBinder = GWT.create(AnnotationsWidgetUiBinder.class);

	interface AnnotationsWidgetUiBinder extends UiBinder<Widget, AnnotationsWidget> {}

	private static CommentsWidgetUiBinder commentsUiBinder = GWT.create(CommentsWidgetUiBinder.class);

	@UiTemplate("CommentsWidget.ui.xml")
	interface CommentsWidgetUiBinder extends UiBinder<Widget, AnnotationsWidget> {}

	@UiField
	protected ExtendableTextArea newAnnotationText;

	@UiField
	protected UploadWidget uploadWidget;

	@UiField
	protected Widget separator;

	@UiField
	protected VerticalModelWidgetContainer<Annotation, AnnotationTopic> annotationsWidgetContainer;

	@UiFactory
	protected VerticalModelWidgetContainer<Annotation, AnnotationTopic> createAnnotationsContainer() {
		return new VerticalModelWidgetContainer<Annotation, AnnotationTopic>(new AnnotationTopicWidgetFactory(subjectId), new ModelWidgetContainerListener() {
			@Override
			public void onUpdateComplete(final boolean hasChanged) {
				if (hasChanged && updateListener != null) updateListener.onChanged();
			}
		});
	}

	private final UUID subjectId;

	private ActionExecutionListener actionsListener;

	private UpdateListener updateListener;

	private final boolean enableComments;

	public AnnotationsWidget(final UUID subjectId) {
		this(uiBinder, subjectId, true);
	}

	private AnnotationsWidget(final UiBinder<Widget, AnnotationsWidget> binder, final UUID subjectId, final boolean enableComments) {
		this.subjectId = subjectId;
		this.enableComments = enableComments;
		initWidget(binder.createAndBindUi(this));
		uploadWidget.setActionUrl("/application/file/upload");
	}

	public static AnnotationsWidget forComments(final UUID subjectId) {
		return new AnnotationsWidget(commentsUiBinder, subjectId, false);
	}

	@Override
	protected void onLoad() {
		uploadWidget.setVisible(enableComments);
		update();

		getActionExecutionService().addActionExecutionListener(getListener());
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(actionsListener);
	}

	@UiHandler("newAnnotationText")
	protected void onNewAnnotationTextKeyDown(final KeyDownEvent e) {
		if (!new Shortcut(BrowserKeyCodes.KEY_ENTER).with(ControlModifier.PRESSED).accepts(e.getNativeEvent())) return;
		e.preventDefault();
		e.stopPropagation();

		addAnnotation();
		newAnnotationText.setText("");
	}

	public void setFocus(final boolean b) {
		newAnnotationText.setFocus(b);
	}

	public int getWidgetCount() {
		return annotationsWidgetContainer.getWidgetCount();
	}

	public void setUpdateListener(final UpdateListener listener) {
		this.updateListener = listener;
	}

	private void addAnnotation() {
		final String message = newAnnotationText.getText().trim();
		final String fileName = uploadWidget.getFilename();
		if (message.isEmpty() && fileName.isEmpty()) return;

		uploadWidget.submitForm(new UploadWidgetListener() {
			@Override
			public void onUploadCompleted(final UUID fileRepresentationId) {
				uploadWidget.setUploadFieldVisible(false);
				final AnnotationType type = enableComments ? AnnotationType.SIMPLE : AnnotationType.COMMENT;
				getProvider().getAnnotationService().createAnnotationFor(subjectId, message, fileRepresentationId, type);
			}
		});
	}

	private void update() {
		annotationsWidgetContainer.update(getAnnotationService().getAnnotationsFor(subjectId));
	}

	private ActionExecutionListener getListener() {
		if (actionsListener != null) return actionsListener;

		return actionsListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
				if (action instanceof AnnotationAction && action.getReferenceId().equals(subjectId)) update();
				if (action instanceof ImpedimentAction && action.getReferenceId().equals(subjectId)) update();
			}
		};
	}

	private AnnotationService getAnnotationService() {
		return getProvider().getAnnotationService();
	}

	private ActionExecutionService getActionExecutionService() {
		return getProvider().getActionExecutionService();
	}

	private ClientServiceProvider getProvider() {
		return ClientServiceProvider.getInstance();
	}

	public interface UpdateListener {
		void onChanged();
	}

	public void setReadOnly(final boolean b) {
		final boolean visible = !b;
		uploadWidget.setVisible(enableComments && visible);
		newAnnotationText.setVisible(visible);
		separator.setVisible(visible);
	}

}
