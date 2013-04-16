package br.com.oncast.ontrack.client.ui.components.details;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.AnnotationsWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ChecklistsContainerWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ScopeDetailWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.SubjectDetailWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.DescriptionRichTextLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.release.ReleaseDetailsInBlockWidget;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.client.utils.jquery.JQuery;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.client.utils.ui.ElementUtils;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.DescriptionAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.description.exceptions.DescriptionNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
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

public class DetailsPanel extends Composite implements HasCloseHandlers<DetailsPanel>, PopupAware {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static final AnnotationsPanelMessages messages = GWT.create(AnnotationsPanelMessages.class);

	private static DetailsPanelUiBinder uiBinder = GWT.create(DetailsPanelUiBinder.class);

	interface DetailsPanelUiBinder extends UiBinder<Widget, DetailsPanel> {}

	@UiField
	SpanElement humanId;

	@UiField(provided = true)
	SubjectDetailWidget subjectDetails;

	@UiField(provided = true)
	EditableLabel subjectTitle;

	@UiField(provided = true)
	DescriptionRichTextLabel descriptionLabel;

	@UiField(provided = true)
	AnnotationsWidget annotations;

	@UiField
	FocusPanel rootPanel;

	@UiField
	ChecklistsContainerWidget checklist;

	@UiField
	DivElement releaseContainer;

	@UiField
	DivElement releaseTag;

	private final UUID subjectId;

	private com.google.web.bindery.event.shared.HandlerRegistration handlerRegistration;

	private DetailsPanel(final SubjectDetailWidget detailWidget, final UUID subjectId, final String subjectDescription, final String headerTagDescription,
			final String humanId) {
		this.subjectId = subjectId;
		subjectTitle = new EditableLabel(new EditableLabelEditionHandler() {

			@Override
			public boolean onEditionRequest(final String text) {
				final ProjectContext projectContext = ClientServices.getCurrentProjectContext();
				final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.actionExecution();
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

			@Override
			public void onEditionStart() {}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}
		});

		descriptionLabel = new DescriptionRichTextLabel(new EditableLabelEditionHandler() {
			@Override
			public boolean onEditionRequest(final String text) {
				SERVICE_PROVIDER.details().updateDescription(subjectId, text);
				return true;
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}

			@Override
			public void onEditionStart() {}

		});

		subjectTitle.setTitle(messages.doubleClickToEdit());

		subjectDetails = detailWidget;
		annotations = new AnnotationsWidget(subjectId);
		initWidget(uiBinder.createAndBindUi(this));

		checklist.setSubjectId(subjectId);
		subjectTitle.setValue(subjectDescription);

		try {
			final Description description = ClientServices.getCurrentProjectContext().findDescriptionFor(subjectId);
			updateDescription(description.getDescription());
		}
		catch (final DescriptionNotFoundException e) {}

		setHeaderTagDescription(headerTagDescription);
		setHumanId(humanId);
	}

	private void setHumanId(final String humanId) {
		ElementUtils.setVisible(this.humanId, !humanId.isEmpty());
		this.humanId.setInnerText(humanId);
		subjectTitle.getElement().getStyle().setLeft(humanId.isEmpty() ? 9 : 36, Unit.PX);
	}

	private void setHeaderTagDescription(final String tagDescription) {
		ElementUtils.setVisible(releaseContainer, !tagDescription.isEmpty());
		releaseTag.setInnerText(tagDescription);
	}

	private void updateDescription(final String description) {
		descriptionLabel.setText(description);
	}

	public static DetailsPanel forRelease(final Release release) {
		return new DetailsPanel(new ReleaseDetailsInBlockWidget(release), release.getId(), release.getDescription(), "", "");
	}

	public static DetailsPanel forScope(final Scope scope) {
		final Release release = scope.getRelease();
		final String humanId = ClientServices.getCurrentProjectContext().getHumanId(scope);
		return new DetailsPanel(new ScopeDetailWidget(scope), scope.getId(), scope.getDescription(), release == null ? "" : release.getDescription(), humanId);
	}

	@UiHandler("rootPanel")
	protected void onKeyDown(final KeyDownEvent e) {
		for (final UndoRedoShortCutMapping m : UndoRedoShortCutMapping.values())
			if (m.getShortcut().accepts(e.getNativeEvent())) return;
		e.stopPropagation();
		if (BrowserKeyCodes.KEY_ESCAPE == e.getNativeKeyCode()) hide();
	}

	@UiHandler("closeIcon")
	protected void onCloseClick(final ClickEvent e) {
		hide();
	}

	@UiHandler("addAnnotationButton")
	protected void onAddAnnotationClick(final ClickEvent e) {
		annotations.enterEditMode();
	}

	@UiHandler("addChecklistButton")
	protected void onAddChecklistClick(final ClickEvent e) {
		checklist.enterEditMode();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<DetailsPanel> handler) {
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
				CloseEvent.fire(DetailsPanel.this, DetailsPanel.this);
			}
		});
	}

	public void registerActionExecutionListener() {
		unregisterActionExecutionListener();
		handlerRegistration = SERVICE_PROVIDER.actionExecution().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof DescriptionAction && action.getReferenceId().equals(subjectId)) updateDescription(((DescriptionAction) action)
						.getDescription());
			}
		});
	}

	public void unregisterActionExecutionListener() {
		if (handlerRegistration == null) return;
		handlerRegistration.removeHandler();
		handlerRegistration = null;
	}
}
