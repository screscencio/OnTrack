package br.com.oncast.ontrack.client.ui.components.scopetree.helper;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.BindReleaseInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareEffortInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareProgressInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareValueInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertChildInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertSiblingDownInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.NodeEditionInternalAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FloatingActionMenu extends Composite {

	private static FloatingActionMenuUiBinder uiBinder = GWT.create(FloatingActionMenuUiBinder.class);

	interface FloatingActionMenuUiBinder extends UiBinder<Widget, FloatingActionMenu> {}

	@UiField
	Button btnEdit;
	@UiField
	Button btnNChild;
	@UiField
	Button btnNSibli;
	@UiField
	Button btnDelete;
	@UiField
	Button btnDetail;
	@UiField
	Button btnRelease;
	@UiField
	Button btnEffort;
	@UiField
	Button btnProgress;
	@UiField
	Button btnValue;
	@UiField
	Label spacer;

	private Scope scope;
	private final FloatingMenuActionHandler actionHandler;
	private ProjectContext projectContext;

	public FloatingActionMenu(final FloatingMenuActionHandler actionHandler) {
		this.actionHandler = actionHandler;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setReferencedScope(final Scope scope, final ProjectContext projectContext) {
		this.scope = scope;
		this.projectContext = projectContext;

		final boolean visibility = !scope.isRoot();
		btnNSibli.setVisible(visibility);
		btnDelete.setVisible(visibility);
		spacer.setVisible(visibility);
	}

	@UiHandler("btnEdit")
	protected void onEditClick(final ClickEvent event) {
		if (scope == null) return;
		actionHandler.onInternalAction(new NodeEditionInternalAction(scope));
	}

	@UiHandler("btnNChild")
	protected void onNChildClick(final ClickEvent event) {
		if (scope == null) return;
		actionHandler.onInternalAction(new InsertChildInternalAction(scope));
	}

	@UiHandler("btnNSibli")
	protected void onNewSiblingClick(final ClickEvent event) {
		if (scope == null) return;
		actionHandler.onInternalAction(new InsertSiblingDownInternalAction(scope));
	}

	@UiHandler("btnDelete")
	protected void onDeleteClick(final ClickEvent event) {
		if (scope == null) return;
		actionHandler.onUserActionExecutionRequest(new ScopeRemoveAction(scope.getId()));
	}

	@UiHandler("btnDetail")
	protected void onDetailClick(final ClickEvent event) {
		if (scope == null) return;
		ClientServiceProvider.get().details().showAnnotationsFor(scope.getId());
		this.setVisible(false);
	}

	@UiHandler("btnRelease")
	protected void onReleaseBindClick(final ClickEvent event) {
		if (scope == null) return;
		actionHandler.onInternalAction(new BindReleaseInternalAction(scope, projectContext));
	}

	@UiHandler("btnEffort")
	protected void onEffortDeclareClick(final ClickEvent event) {
		if (scope == null) return;
		actionHandler.onInternalAction(new DeclareEffortInternalAction(scope, projectContext));
	}

	@UiHandler("btnProgress")
	protected void onProgressDeclareClick(final ClickEvent event) {
		if (scope == null) return;
		actionHandler.onInternalAction(new DeclareProgressInternalAction(scope, projectContext));
	}

	@UiHandler("btnValue")
	protected void onValueDeclareClick(final ClickEvent event) {
		if (scope == null) return;
		actionHandler.onInternalAction(new DeclareValueInternalAction(scope, projectContext));
	}

}
