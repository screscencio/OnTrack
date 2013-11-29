package br.com.oncast.ontrack.server.services.metrics;

import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ServerAnalytics {

	void activate();

	void deactivate();

	void onActionExecuted(UserAction action);

	void onActionConflicted(UserAction action);

	void onProjectMemberRemoved(UUID projectId, UUID userId, User remover);

	void onProjectMemberInvited(UUID projectId, UUID userId, User invitor);

	void onProjectCreated(User creator, UUID projectId);

	void onProjectRemoved(User remover, UUID projectId);

	void onProjectCreationRequested(User user);

	void onFeedbackReceived(User user, String feedbackMessage);

	void onFileUploaded(User uploader, FileRepresentation fileRepresentation);

	void onNewUserCreated(User user);

	void onGlobalProfileUpdated(User user);

	void onOnlineUsersCountChanged(int count, boolean entered);

	void onActiveConnectionsCountChanged(int count, boolean connected);

	void onMigrationExecution(String versionFrom, String versionTo, long timeSpent);

	void onImportXmlLoad(long xmlFileLenght, long timeSpent);

	void onUsersImported(int newUsersCount, long timeSpent);

	void onProjectPersisted(ProjectRepresentation representation, int actionsCount, long timeSpent);

	void onProjectAuthorizationsImported(int projectAuthorizationsCount, long timeSpent);

	void onProjectLoadedForMigration(ProjectRepresentation representation, int actionsCount, long timeSpent);

	void onProjectLoadForMigrationError(ProjectRepresentation representation, Exception e);

	void onNotificationsImported(int notificationsCount, long timeSpent);

}
