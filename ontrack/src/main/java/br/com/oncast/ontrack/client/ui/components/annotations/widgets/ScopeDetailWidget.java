package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeDetailWidget extends Composite implements SubjectDetailWidget<Scope> {

	private static ScopeDetailWidgetUiBinder uiBinder = GWT.create(ScopeDetailWidgetUiBinder.class);

	interface ScopeDetailWidgetUiBinder extends UiBinder<Widget, ScopeDetailWidget> {}

	public ScopeDetailWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label title;

	private Scope scope;

	private ActionExecutionListener actionExecutionListener;

	@Override
	public void setSubject(final Scope scope) {
		this.scope = scope;
		update();
	}

	@Override
	protected void onLoad() {
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
	}

	private ActionExecutionService getActionExecutionService() {
		return ClientServiceProvider.getInstance().getActionExecutionService();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof ScopeUpdateAction && action.getReferenceId().equals(scope.getId())) update();
			}
		};
		return actionExecutionListener;
	}

	private void update() {
		title.setText(scope.getDescription());
	}

}
