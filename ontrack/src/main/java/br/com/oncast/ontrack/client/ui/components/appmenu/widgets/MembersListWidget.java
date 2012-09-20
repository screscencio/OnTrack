package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MembersListWidget extends Composite {

	private static MembersListWidgetUiBinder uiBinder = GWT.create(MembersListWidgetUiBinder.class);

	interface MembersListWidgetUiBinder extends UiBinder<Widget, MembersListWidget> {}

	public MembersListWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		updateMembersList();

		ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof TeamAction) updateMembersList();
			}
		});
	}

	@UiField
	VerticalModelWidgetContainer<User, ProjectMemberWidget> membersList;

	@UiFactory
	public VerticalModelWidgetContainer<User, ProjectMemberWidget> getMembersList() {
		return new VerticalModelWidgetContainer<User, ProjectMemberWidget>(new ModelWidgetFactory<User, ProjectMemberWidget>() {

			@Override
			public ProjectMemberWidget createWidget(final User modelBean) {
				return new ProjectMemberWidget(modelBean);
			}
		});
	}

	private void updateMembersList() {
		membersList.update(ClientServiceProvider.getInstance().getContextProviderService().getCurrentProjectContext().getUsers());
	}
}
