package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.ArrayList;
import java.util.List;
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
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MembersListWidget extends Composite {

	private static MembersListWidgetUiBinder uiBinder = GWT.create(MembersListWidgetUiBinder.class);

	interface MembersListWidgetUiBinder extends UiBinder<Widget, MembersListWidget> {}

	@UiField(provided = true)
	VerticalModelWidgetContainer<User, ProjectMemberWidget> membersList;

	@UiField(provided = true)
	VerticalModelWidgetContainer<User, ProjectMemberWidget> activeMembersList;

	public MembersListWidget() {
		membersList = new VerticalModelWidgetContainer<User, ProjectMemberWidget>(new ModelWidgetFactory<User, ProjectMemberWidget>() {
			@Override
			public ProjectMemberWidget createWidget(final User modelBean) {
				return new ProjectMemberWidget(modelBean, UserStatus.INACTIVE);
			}
		});

		activeMembersList = new VerticalModelWidgetContainer<User, ProjectMemberWidget>(new ModelWidgetFactory<User, ProjectMemberWidget>() {
			@Override
			public ProjectMemberWidget createWidget(final User modelBean) {
				return new ProjectMemberWidget(modelBean, UserStatus.ACTIVE);
			}
		});

		initWidget(uiBinder.createAndBindUi(this));

		ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof TeamAction) updateMembersList();
			}
		});
	}

	@Override
	protected void onLoad() {
		updateMembersList();
	}

	private void updateMembersList() {
		final ProjectContext currentProjectContext = ClientServiceProvider.getInstance().getContextProviderService().getCurrentProjectContext();

		ClientServiceProvider.getInstance().getUsersStatusService().getActiveUsers(new AsyncCallback<Set<User>>() {

			@Override
			public void onSuccess(final Set<User> result) {
				activeMembersList.update(new ArrayList<User>(result));

				final List<User> users = new ArrayList<User>();
				for (final User user : currentProjectContext.getUsers()) {
					if (!result.contains(user)) users.add(user);
				}
				membersList.update(users);
			}

			@Override
			public void onFailure(final Throwable caught) {
				membersList.update(currentProjectContext.getUsers());
			}
		});
	}
}
