package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MembersListWidget extends Composite {

	private static MembersListWidgetUiBinder uiBinder = GWT.create(MembersListWidgetUiBinder.class);

	interface MembersListWidgetUiBinder extends UiBinder<Widget, MembersListWidget> {}

	@UiField(provided = true)
	VerticalModelWidgetContainer<User, ProjectMemberWidget> membersList;

	@UiField(provided = true)
	VerticalModelWidgetContainer<User, ProjectMemberWidget> activeMembersList;

	@UiField
	SimplePanel loadingPanel;

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

		setupMembersList();

		ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof TeamAction) updateMembersList(ClientServiceProvider.getInstance().getUsersStatusService().getActiveUsers());
			}
		});
	}

	private void setupMembersList() {
		loadingPanel.setVisible(true);
		ClientServiceProvider.getInstance().getUsersStatusService().register(new UsersStatusChangeListener() {

			@Override
			public void onActiveUsersListUnavailable(final Throwable caught) {
				loadingPanel.setVisible(false);
				updateMembersList(new HashSet<User>());
			}

			@Override
			public void onActiveUsersListLoaded(final Set<User> activeUsers) {
				loadingPanel.setVisible(false);
				updateMembersList(activeUsers);
			}
		});
	}

	private void updateMembersList(final Set<User> activeUsers) {
		final ProjectContext currentProjectContext = ClientServiceProvider.getInstance().getContextProviderService().getCurrentProjectContext();
		activeMembersList.update(new ArrayList<User>(activeUsers));

		final List<User> users = new ArrayList<User>();
		for (final User user : currentProjectContext.getUsers()) {
			if (!activeUsers.contains(user)) users.add(user);
		}
		membersList.update(users);
	}
}
