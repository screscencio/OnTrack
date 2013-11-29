package br.com.oncast.ontrack.server.services.metrics;

import br.com.oncast.ontrack.server.configuration.Configurations;
import br.com.oncast.ontrack.shared.metrics.MetricsTokenizer;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.concurrent.Future;

import com.brsanthu.googleanalytics.EventHit;
import com.brsanthu.googleanalytics.ExceptionHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.GoogleAnalyticsRequest;
import com.brsanthu.googleanalytics.GoogleAnalyticsResponse;
import com.brsanthu.googleanalytics.RequestProvider;
import com.brsanthu.googleanalytics.TimingHit;

public class GoogleAnalyticsServerAnalytics implements ServerAnalytics {

	private static final int DIMENSION_USER = 1;
	private static final int DIMENSION_PROJECT = 2;

	private GoogleAnalytics ga;
	private final String trackingId;

	public GoogleAnalyticsServerAnalytics() {
		trackingId = Configurations.get().getGoogleAnalyticsTrackingId();
		activate();
	}

	@Override
	public void activate() {
		ga = new GoogleAnalytics(trackingId);
	}

	@Override
	public void deactivate() {
		ga = new NullGoogleAnalytics();
	}

	@Override
	public void onActionExecuted(final UserAction action) {
		send(action.getUserId(), action.getProjectId(), new EventHit("action", "server_side_execution").eventLabel(className(action.getModelAction())));
	}

	@Override
	public void onActionConflicted(final UserAction action) {
		send(action.getUserId(), action.getProjectId(), new EventHit("action", "server_side_confict").eventLabel(className(action.getModelAction())));
	}

	@Override
	public void onProjectMemberRemoved(final UUID projectId, final UUID removedUserId, final User remover) {
		send(remover, projectId, new EventHit("project_member", "remove").eventLabel(removedUserId.toString()));
	}

	@Override
	public void onProjectMemberInvited(final UUID projectId, final UUID invitedUserId, final User invitor) {
		send(invitor, projectId, new EventHit("project_member", "invite").eventLabel(invitedUserId.toString()));
	}

	@Override
	public void onProjectCreated(final User creator, final UUID projectId) {
		send(creator, projectId, new EventHit("project", "create").eventLabel(projectId.toString()));
	}

	@Override
	public void onProjectRemoved(final User remover, final UUID projectId) {
		send(remover, projectId, new EventHit("project", "remove").eventLabel(projectId.toString()));
	}

	@Override
	public void onProjectCreationRequested(final User user) {
		send(user, new EventHit("project", "creation_request"));
	}

	@Override
	public void onFeedbackReceived(final User user, final String message) {
		send(user, new EventHit("feedback", "send").eventLabel(message));
	}

	@Override
	public void onFileUploaded(final User uploader, final FileRepresentation fileRepresentation) {
		// FIXME send the file length
		send(uploader, fileRepresentation.getProjectId(), new EventHit("file", "upload").eventLabel(fileExtension(fileRepresentation)));
	}

	private String fileExtension(final FileRepresentation fileRepresentation) {
		final String[] split = fileRepresentation.getFileName().split("\\.");
		if (split.length < 2) return "Unknown extension";
		return split[split.length - 1];
	}

	@Override
	public void onNewUserCreated(final User user) {
		ga.postAsync(new EventHit("user", "create").eventLabel(user.getGlobalProfile().name()));
	}

	@Override
	public void onGlobalProfileUpdated(final User user) {
		ga.postAsync(new EventHit("user", "global_profile_update").eventLabel(user.getGlobalProfile().name()));
	}

	@Override
	public void onOnlineUsersCountChanged(final int count, final boolean entered) {
		ga.postAsync(new EventHit("ontrack", "online_users_count_change", entered ? "entered" : "left", count));
	}

	@Override
	public void onActiveConnectionsCountChanged(final int count, final boolean connected) {
		ga.postAsync(new EventHit("ontrack", "active_connections_count_change", connected ? "connected" : "desconnected", count));
	}

	@Override
	public void onMigrationExecution(final String versionFrom, final String versionTo, final long timeSpent) {
		sendTimingHit("importer_migration_applied", versionFrom + " - " + versionTo, timeSpent);
	}

	@Override
	public void onImportXmlLoad(final long xmlFileLenght, final long timeSpent) {
		sendTimingHit("importer_xml_read", xmlFileLenght + "", timeSpent);
	}

	@Override
	public void onUsersImported(final int newUsersCount, final long timeSpent) {
		sendTimingHit("importer_users_imported", newUsersCount + "", timeSpent);
	}

	@Override
	public void onProjectPersisted(final ProjectRepresentation representation, final int actionsCount, final long timeSpent) {
		send(representation, timingHit("importer_project_persisted", actionsCount + "", timeSpent));
	}

	@Override
	public void onProjectAuthorizationsImported(final int projectAuthorizationsCount, final long timeSpent) {
		sendTimingHit("importer_project_authorization_imported", projectAuthorizationsCount + "", timeSpent);
	}

	@Override
	public void onProjectLoadedForMigration(final ProjectRepresentation representation, final int actionsCount, final long timeSpent) {
		send(representation, timingHit("importer_project_loaded", actionsCount + "", timeSpent));
	}

	@Override
	public void onProjectLoadForMigrationError(final ProjectRepresentation representation, final Exception e) {
		send(representation, new ExceptionHit(e.toString()));
	}

	@Override
	public void onNotificationsImported(final int notificationsCount, final long timeSpent) {
		sendTimingHit("importer_notifications_imported", "" + notificationsCount, timeSpent);
	}

	private void sendTimingHit(final String category, final String label, final long timeSpent) {
		ga.postAsync(timingHit(category, label, timeSpent));
	}

	private TimingHit timingHit(final String category, final String label, final long timeSpent) {
		final TimingHit hit = new TimingHit();
		hit.userTimingCategory("ontrack");
		hit.userTimingVariableName(category);
		hit.userTimingLabel(label);
		hit.userTimingTime((int) timeSpent);
		return hit;
	}

	private <T extends GoogleAnalyticsRequest<T>> void send(final User user, final UUID projectId, final T request) {
		send(user.getId(), projectId, request);
	}

	private <T extends GoogleAnalyticsRequest<T>> void send(final UUID userId, final UUID projectId, final T request) {
		request.customDimention(DIMENSION_PROJECT, projectId.toString());
		sendWithUser(userId, request);
	}

	private <T extends GoogleAnalyticsRequest<T>> void send(final User user, final T request) {
		sendWithUser(user.getId(), request);
	}

	private <T extends GoogleAnalyticsRequest<T>> void send(final ProjectRepresentation representation, final T request) {
		sendWithProject(representation.getId(), request);
	}

	private <T extends GoogleAnalyticsRequest<T>> void sendWithProject(final UUID projectId, final T request) {
		request.customDimention(DIMENSION_PROJECT, projectId.toString());
		ga.postAsync(request);
	}

	private <T extends GoogleAnalyticsRequest<T>> void sendWithUser(final UUID userId, final T request) {
		request.customDimention(DIMENSION_USER, userId.toString());
		ga.postAsync(request);
	}

	private String className(final ModelAction action) {
		return MetricsTokenizer.getClassSimpleName(action);
	}

	@SuppressWarnings("rawtypes")
	private class NullGoogleAnalytics extends GoogleAnalytics {

		public NullGoogleAnalytics() {
			super(trackingId);
		}

		@Override
		public GoogleAnalyticsResponse post(final GoogleAnalyticsRequest request) {
			return null;
		}

		@Override
		public Future<GoogleAnalyticsResponse> postAsync(final GoogleAnalyticsRequest request) {
			return null;
		}

		@Override
		public Future<GoogleAnalyticsResponse> postAsync(final RequestProvider requestProvider) {
			return null;
		}

	}

}
