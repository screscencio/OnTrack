package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationsWidget extends Composite {

	private static AnnotationsWidgetUiBinder uiBinder = GWT.create(AnnotationsWidgetUiBinder.class);

	interface AnnotationsWidgetUiBinder extends UiBinder<Widget, AnnotationsWidget> {}

	@UiField
	protected FocusPanel focusPanel;

	@UiField
	protected ExtendableTextArea newAnnotationText;

	@UiField
	protected VerticalModelWidgetContainer<Annotation, AnnotationTopic> annotations;

	@UiFactory
	protected VerticalModelWidgetContainer<Annotation, AnnotationTopic> createAnnotationsContainer() {
		return new VerticalModelWidgetContainer<Annotation, AnnotationTopic>(new ModelWidgetFactory<Annotation, AnnotationTopic>() {

			@Override
			public AnnotationTopic createWidget(final Annotation modelBean) {
				return new AnnotationTopic(modelBean);
			}

		}, new ModelWidgetContainerListener() {
			@Override
			public void onUpdateComplete(final boolean hasChanged) {}
		});
	}

	private UUID subjectId;

	private ActionExecutionListener listener;

	public AnnotationsWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad() {
		getActionExecutionService().addActionExecutionListener(getListener());
		update();
	}

	public void setSubjectId(final UUID subjectId) {
		this.subjectId = subjectId;
		update();
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(listener);
	}

	@UiHandler("newAnnotationText")
	protected void onNewAnnotationTextKeyDown(final KeyDownEvent e) {
		if (BrowserKeyCodes.KEY_ENTER != e.getNativeKeyCode() || !e.isControlKeyDown()) return;
		e.preventDefault();

		addAnnotation();
		newAnnotationText.setText("");
	}

	@UiHandler("focusPanel")
	protected void disableGlobalShortcuts(final KeyDownEvent e) {
		if (BrowserKeyCodes.KEY_ESCAPE == e.getNativeKeyCode()) return;

		e.stopPropagation();
	}

	public void setFocus(final boolean b) {
		newAnnotationText.setFocus(true);
	}

	private void addAnnotation() {
		final String message = newAnnotationText.getText().trim();
		if (message.isEmpty()) return;

		getProvider().getAnnotationService().createAnnotationFor(subjectId, message);
	}

	private void update() {
		annotations.update(getCurrentProjectContext().findAnnotationsFor(subjectId));
	}

	private ActionExecutionListener getListener() {
		if (listener != null) return listener;

		return listener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof AnnotationAction && subjectId.equals(action.getReferenceId())) {
					for (final Annotation a : context.findAnnotationsFor(subjectId))
						System.out.println("Widget\n" + a.getAuthor().getEmail() + ": " + a.getMessage());
					update();
				}
			}
		};
	}

	private ProjectContext getCurrentProjectContext() {
		return getProvider().getContextProviderService().getCurrentProjectContext();
	}

	private ActionExecutionService getActionExecutionService() {
		return getProvider().getActionExecutionService();
	}

	private ClientServiceProvider getProvider() {
		return ClientServiceProvider.getInstance();
	}
}
