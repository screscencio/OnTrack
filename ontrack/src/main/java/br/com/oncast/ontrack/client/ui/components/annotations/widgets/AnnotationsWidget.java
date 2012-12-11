package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.annotations.AnnotationService;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ExtendableTextArea.ExpansionListener;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.UploadWidget.UploadWidgetListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.utils.jquery.JQuery;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationsWidget extends Composite {

	private static AnnotationsWidgetUiBinder uiBinder = GWT.create(AnnotationsWidgetUiBinder.class);

	interface AnnotationsWidgetUiBinder extends UiBinder<Widget, AnnotationsWidget> {}

	private static final AnnotationsWidgetMessages messages = GWT.create(AnnotationsWidgetMessages.class);

	@UiField
	protected ExtendableTextArea newAnnotationText;

	@UiField
	protected UploadWidget uploadWidget;

	@UiField
	protected Widget separator;

	@UiField
	protected FocusPanel createNotificationButton;

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

	Timer buttonTimer = new Timer() {

		@Override
		public void run() {
			JQuery.jquery(createNotificationButton).clearQueue().fadeOut(300, new AnimationCallback() {

				@Override
				public void onComplete() {
					createNotificationButton.setVisible(false);
					newAnnotationText.hideRichTextArea();
				}
			});
		}
	};

	private final UUID subjectId;

	private ActionExecutionListener actionsListener;

	public AnnotationsWidget(final UUID subjectId) {
		this.subjectId = subjectId;
		initWidget(uiBinder.createAndBindUi(this));
		createNotificationButton.setVisible(false);
		uploadWidget.setVisible(false);
		uploadWidget.setActionUrl("/application/file/upload");
		createNotificationButton.setTitle(messages.createAnnotation());
		newAnnotationText.registerExpansionListener(new ExpansionListener() {

			@Override
			public void onExpandded() {
				createNotificationButton.setVisible(true);
				uploadWidget.setVisible(true);
			}

			@Override
			public void onShrinked() {
				createNotificationButton.setVisible(false);
				uploadWidget.setVisible(false);
			}
		});
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

	@UiHandler("createNotificationButton")
	protected void onNewAnnotationClick(final ClickEvent event) {
		addAnnotation();
	}

	@UiHandler("newAnnotationText")
	protected void onNewAnnotationTextKeyDown(final KeyDownEvent e) {
		if (!new Shortcut(BrowserKeyCodes.KEY_ENTER).with(ControlModifier.PRESSED).accepts(e.getNativeEvent())) return;
		e.preventDefault();
		e.stopPropagation();

		addAnnotation();
	}

	public void setFocus(final boolean b) {
		newAnnotationText.setFocus(b);
	}

	public int getWidgetCount() {
		return annotationsWidgetContainer.getWidgetCount();
	}

	private void addAnnotation() {
		final String message = newAnnotationText.getText().trim();
		final String fileName = uploadWidget.getFilename();
		if (message.trim().isEmpty() && fileName.isEmpty()) return;

		uploadWidget.submitForm(new UploadWidgetListener() {
			@Override
			public void onUploadCompleted(final UUID fileRepresentationId) {
				uploadWidget.setUploadFieldVisible(false);
				getProvider().getAnnotationService().createAnnotationFor(subjectId, message, fileRepresentationId);
				newAnnotationText.setText("");
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

}
