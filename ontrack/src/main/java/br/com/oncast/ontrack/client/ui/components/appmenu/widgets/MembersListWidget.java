package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.user.UsersStatusService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
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
	ModelWidgetContainer<UserRepresentation, ProjectMemberWidget> membersList;

	@UiField(provided = true)
	ModelWidgetContainer<UserRepresentation, ProjectMemberWidget> activeMembersList;

	@UiField(provided = true)
	ModelWidgetContainer<UserRepresentation, ProjectMemberWidget> onlineMembersList;

	@UiField
	SimplePanel loadingPanel;

	public MembersListWidget() {
		membersList = new ModelWidgetContainer<UserRepresentation, ProjectMemberWidget>(new ModelWidgetFactory<UserRepresentation, ProjectMemberWidget>() {
			@Override
			public ProjectMemberWidget createWidget(final UserRepresentation modelBean) {
				return new ProjectMemberWidget(modelBean);
			}
		});

		activeMembersList = new ModelWidgetContainer<UserRepresentation, ProjectMemberWidget>(
				new ModelWidgetFactory<UserRepresentation, ProjectMemberWidget>() {
					@Override
					public ProjectMemberWidget createWidget(final UserRepresentation modelBean) {
						return new ProjectMemberWidget(modelBean);
					}
				});

		onlineMembersList = new ModelWidgetContainer<UserRepresentation, ProjectMemberWidget>(
				new ModelWidgetFactory<UserRepresentation, ProjectMemberWidget>() {
					@Override
					public ProjectMemberWidget createWidget(final UserRepresentation modelBean) {
						return new ProjectMemberWidget(modelBean);
					}
				});

		initWidget(uiBinder.createAndBindUi(this));

		setupMembersList();

		ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof TeamAction) {
					final UsersStatusService usersStatusService = ClientServiceProvider.getInstance().getUsersStatusService();
					updateMembersList(usersStatusService.getActiveUsers(), usersStatusService.getOnlineUsers());
				}
			}
		});
	}

	private void setupMembersList() {
		loadingPanel.setVisible(true);
		ClientServiceProvider.getInstance().getUsersStatusService().register(new UsersStatusChangeListener() {

			@Override
			public void onUsersStatusListUnavailable(final Throwable caught) {
				loadingPanel.setVisible(false);
				updateMembersList(new TreeSet<UserRepresentation>(), new TreeSet<UserRepresentation>());
			}

			@Override
			public void onUsersStatusListsUpdated(final SortedSet<UserRepresentation> activeUsers, final SortedSet<UserRepresentation> onlineUsers) {
				loadingPanel.setVisible(false);
				updateMembersList(activeUsers, onlineUsers);
			}

		});
	}

	private void updateMembersList(final SortedSet<UserRepresentation> activeUsers, final SortedSet<UserRepresentation> onlineUsers) {
		final ProjectContext currentProjectContext = ClientServiceProvider.getInstance().getContextProviderService().getCurrentProjectContext();
		activeMembersList.update(new ArrayList<UserRepresentation>(activeUsers));

		final ArrayList<UserRepresentation> onlineAndNotActiveUsers = new ArrayList<UserRepresentation>();
		for (final UserRepresentation user : onlineUsers) {
			if (!activeUsers.contains(user)) onlineAndNotActiveUsers.add(user);
		}
		onlineMembersList.update(onlineAndNotActiveUsers);

		final List<UserRepresentation> users = new ArrayList<UserRepresentation>();
		for (final UserRepresentation user : currentProjectContext.getUsers()) {
			if (!activeUsers.contains(user) && !onlineUsers.contains(user)) users.add(user);
		}
		membersList.update(users);
	}
}
