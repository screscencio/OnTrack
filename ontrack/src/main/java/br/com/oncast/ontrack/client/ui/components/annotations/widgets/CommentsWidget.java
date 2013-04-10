package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
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
import com.google.gwt.user.client.ui.Widget;

public class CommentsWidget extends Composite {

	private static CommentsWidgetUiBinder uiBinder = GWT.create(CommentsWidgetUiBinder.class);

	interface CommentsWidgetUiBinder extends UiBinder<Widget, CommentsWidget> {}

	@UiField
	protected ExtendableTextArea newCommentText;

	@UiField
	protected Widget separator;

	@UiField
	protected ModelWidgetContainer<Annotation, AnnotationComment> commentsWidgetContainer;

	@UiFactory
	protected ModelWidgetContainer<Annotation, AnnotationComment> createAnnotationsContainer() {
		return new ModelWidgetContainer<Annotation, AnnotationComment>(new ModelWidgetFactory<Annotation, AnnotationComment>() {
			@Override
			public AnnotationComment createWidget(final Annotation modelBean) {
				return new AnnotationComment(subjectId, modelBean);
			}
		});
	}

	private final UUID subjectId;

	private ActionExecutionListener actionsListener;

	public CommentsWidget(final UUID subjectId) {
		this.subjectId = subjectId;
		initWidget(uiBinder.createAndBindUi(this));
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

	@UiHandler("newCommentText")
	protected void onNewAnnotationTextKeyDown(final KeyDownEvent e) {
		if (!new Shortcut(BrowserKeyCodes.KEY_ENTER).with(ControlModifier.PRESSED).accepts(e.getNativeEvent())) return;
		e.preventDefault();
		e.stopPropagation();

		addComment();
		newCommentText.setText("");
	}

	public void setFocus(final boolean b) {
		newCommentText.setFocus(b);
	}

	public int getWidgetCount() {
		return commentsWidgetContainer.getWidgetCount();
	}

	private void addComment() {
		final String message = newCommentText.getText().trim();
		if (message.trim().isEmpty()) return;

		getProvider().details().createAnnotationFor(subjectId, message, null);
	}

	private void update() {
		commentsWidgetContainer.update(getAnnotationService().getAnnotationsFor(subjectId));
	}

	private ActionExecutionListener getListener() {
		if (actionsListener != null) return actionsListener;

		return actionsListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
				if (action instanceof AnnotationAction && action.getReferenceId().equals(subjectId)) update();
			}
		};
	}

	private DetailService getAnnotationService() {
		return getProvider().details();
	}

	private ActionExecutionService getActionExecutionService() {
		return getProvider().actionExecution();
	}

	private ClientServiceProvider getProvider() {
		return ClientServiceProvider.get();
	}

	public void setReadOnly(final boolean b) {
		final boolean visible = !b;
		newCommentText.setVisible(visible);
		separator.setVisible(visible);

		final List<Annotation> commentsList = getAnnotationService().getAnnotationsFor(subjectId);
		for (final Annotation comment : commentsList) {
			commentsWidgetContainer.getWidgetFor(comment).setReadOnly(b);
		}
	}

}
