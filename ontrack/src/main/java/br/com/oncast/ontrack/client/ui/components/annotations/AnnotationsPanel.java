package br.com.oncast.ontrack.client.ui.components.annotations;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.AnnotationsWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ChecklistsContainerWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ReleaseDetailWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ScopeDetailWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.SubjectDetailWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.utils.jquery.JQuery;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationsPanel extends Composite implements HasCloseHandlers<AnnotationsPanel>, PopupAware {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static final AnnotationsPanelMessages messages = GWT.create(AnnotationsPanelMessages.class);

	private static AnnotationsPanelUiBinder uiBinder = GWT.create(AnnotationsPanelUiBinder.class);

	interface AnnotationsPanelUiBinder extends UiBinder<Widget, AnnotationsPanel> {}

	@UiField(provided = true)
	SubjectDetailWidget subjectDetails;

	@UiField(provided = true)
	EditableLabel subjectTitle;

	@UiField(provided = true)
	AnnotationsWidget annotations;

	@UiField
	FocusPanel rootPanel;

	@UiField
	ChecklistsContainerWidget checklist;

	private AnnotationsPanel(final SubjectDetailWidget detailWidget, final UUID subjectId, final String subjectDescription) {
		subjectTitle = new EditableLabel(new EditableLabelEditionHandler() {

			@Override
			public boolean onEditionRequest(final String text) {
				final ProjectContext projectContext = SERVICE_PROVIDER.getContextProviderService().getCurrentProjectContext();
				final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.getActionExecutionService();
				try {
					projectContext.findScope(subjectId);
					actionExecutionService.onUserActionExecutionRequest(new ScopeUpdateAction(subjectId, text));
				}
				catch (final ScopeNotFoundException e) {
					try {
						projectContext.findRelease(subjectId);
						actionExecutionService
								.onUserActionExecutionRequest(new ReleaseRenameAction(subjectId, text));
					}
					catch (final ReleaseNotFoundException e1) {
						throw new RuntimeException("Impossible to create an editable label for this annotation.");
					}
				}
				return true;
			}
		});

		subjectTitle.setTitle(messages.doubleClickToEdit());

		subjectDetails = detailWidget;
		annotations = new AnnotationsWidget(subjectId);
		initWidget(uiBinder.createAndBindUi(this));

		checklist.setSubjectId(subjectId);
		this.subjectTitle.setValue(subjectDescription);
	}

	public static AnnotationsPanel forRelease(final Release release) {
		return new AnnotationsPanel(new ReleaseDetailWidget(release), release.getId(), release.getDescription());
	}

	public static AnnotationsPanel forScope(final Scope scope) {
		return new AnnotationsPanel(new ScopeDetailWidget(scope), scope.getId(), scope.getDescription());
	}

	@UiHandler("rootPanel")
	protected void onKeyDown(final KeyDownEvent e) {
		if (BrowserKeyCodes.KEY_ESCAPE == e.getNativeKeyCode()) hide();
	}

	@UiHandler("closeIcon")
	protected void onCloseClick(final ClickEvent e) {
		hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<AnnotationsPanel> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		JQuery.jquery(rootPanel).clearQueue().customDropDownAbsolutePositioning(600, new AnimationCallback() {

			@Override
			public void onComplete() {
				annotations.setFocus(true);
			}
		});
	}

	@Override
	public void hide() {
		if (!isVisible()) return;

		JQuery.jquery(rootPanel).clearQueue().customDropUpAbsolutePositioning(400, new AnimationCallback() {

			@Override
			public void onComplete() {
				CloseEvent.fire(AnnotationsPanel.this, AnnotationsPanel.this);
			}
		});
	}

}
