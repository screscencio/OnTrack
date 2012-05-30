package br.com.oncast.ontrack.client.ui.components.annotations;

import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.AnnotationsWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ScopeDetailWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.SubjectDetailWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
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

	private static AnnotationsPanelUiBinder uiBinder = GWT.create(AnnotationsPanelUiBinder.class);

	interface AnnotationsPanelUiBinder extends UiBinder<Widget, AnnotationsPanel> {}

	@UiField(provided = true)
	SubjectDetailWidget<Scope> subjectDetails;

	@UiField
	AnnotationsWidget annotations;

	@UiField
	FocusPanel rootPanel;

	public AnnotationsPanel() {
		subjectDetails = new ScopeDetailWidget();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("rootPanel")
	protected void onKeyDown(final KeyDownEvent e) {
		if (BrowserKeyCodes.KEY_ESCAPE == e.getNativeKeyCode()) hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<AnnotationsPanel> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		annotations.setFocus(true);
	}

	@Override
	public void hide() {
		if (!isVisible()) return;

		CloseEvent.fire(this, this);
	}

	public void setScope(final Scope scope) {
		subjectDetails.setSubject(scope);
		subjectDetails.update();

		annotations.setSubjectId(scope.getId());
		final ProjectContext context = ClientServiceProvider.getInstance().getContextProviderService().getCurrentProjectContext();
		updateAnnotations(context.findAnnotationsFor(scope.getId()));
	}

	public void updateAnnotations(final List<Annotation> annotationsList) {
		annotations.update(annotationsList);
	}

}
