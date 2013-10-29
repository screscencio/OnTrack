package br.com.oncast.ontrack.server.services.metrics;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;
import br.com.oncast.ontrack.shared.services.metrics.ProjectMetrics;
import br.com.oncast.ontrack.shared.services.metrics.UserUsageData;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

public class ServerMetricsService {

	private static final OnTrackStatisticsFactory FACTORY = AutoBeanFactorySource.create(OnTrackStatisticsFactory.class);

	private static final Logger LOGGER = Logger.getLogger(ServerMetricsService.class);

	private final PersistenceService persistenceService;
	private final ClientManager clientManager;
	private final BusinessLogic businessLogic;

	public ServerMetricsService(final PersistenceService persistenceService, final ClientManager clientManager, final BusinessLogic businessLogic) {
		this.persistenceService = persistenceService;
		this.clientManager = clientManager;
		this.businessLogic = businessLogic;
	}

	public long getActionsCountSince(final Date date) {
		try {
			return persistenceService.countActionsSince(date);
		} catch (final PersistenceException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	public Map<String, Integer> getActionsRatio(final Date date) {
		try {
			final Map<String, Integer> map = new HashMap<String, Integer>();
			final List<UserAction> actions = persistenceService.retrieveActionsSince(date);
			for (final UserAction userAction : actions) {
				final String name = userAction.getModelAction().getClass().getSimpleName();
				int count = map.containsKey(name) ? map.get(name) : 0;
				map.put(name, ++count);
			}

			return map;
		} catch (final PersistenceException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	public int getTotalUsersCount() {
		try {
			return persistenceService.retrieveAllUsers().size();
		} catch (final PersistenceException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	public int getTotalProjectsCount() {
		try {
			return persistenceService.retrieveAllProjectRepresentations().size();
		} catch (final PersistenceException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	public List<ProjectMetrics> getActiveProjectsMetrics() {
		final Set<UUID> activeProjects = new HashSet<UUID>();
		final Set<ServerPushConnection> clients = clientManager.getAllClients();
		for (final ServerPushConnection client : clients) {
			final UUID currentProject = clientManager.getCurrentProject(client);
			if (currentProject.isValid()) activeProjects.add(currentProject);
		}

		final List<ProjectMetrics> metrics = new ArrayList<ProjectMetrics>();
		for (final UUID projectId : activeProjects) {
			try {
				metrics.add(getProjectMetrics(projectId));
			} catch (final Exception e) {
				LOGGER.error("unable to get projects metrics", e);
			}
		}
		return metrics;
	}

	public ProjectMetrics getProjectMetrics(final UUID projectId) throws Exception {
		final ProjectMetrics metrics = FACTORY.createProjectMetrics().as();
		final Project project = businessLogic.loadProject(projectId).getProject();
		metrics.setProjectName(project.getProjectRepresentation().getName());
		metrics.setUsersCount(project.getUsers().size());
		final List<Scope> scopes = project.getProjectScope().getAllDescendantScopes();
		metrics.setScopesCount(scopes.size());
		final List<Integer> scopesDepth = new ArrayList<Integer>();
		for (final Scope scope : scopes) {
			if (scope.isLeaf()) scopesDepth.add(countToRoot(scope));
		}
		metrics.setScopesDepth(scopesDepth);

		final List<Release> releases = project.getProjectRelease().getAllReleasesInTemporalOrder();
		metrics.setReleasesCount(releases.size());
		final List<Integer> releasesDuration = new ArrayList<Integer>();
		final List<Integer> releasesDepth = new ArrayList<Integer>();
		final List<Integer> releasesStoryCount = new ArrayList<Integer>();
		final ReleaseEstimator estimator = new ReleaseEstimator(project.getProjectRelease());
		for (final Release release : releases) {
			if (!release.isRoot() && release.isLeaf()) {
				final WorkingDay startDay = estimator.getEstimatedStartDayFor(release);
				final WorkingDay endDay = estimator.getEstimatedEndDayFor(release);
				releasesDuration.add(startDay.countTo(endDay));
				releasesDepth.add(countToRootRelease(release));
			}
			if (release.hasDirectScopes()) {
				releasesStoryCount.add(release.getScopeList().size());
			}
		}
		metrics.setReleasesDepth(releasesDepth);
		metrics.setReleasesDuration(releasesDuration);
		metrics.setStoriesPerRelease(releasesStoryCount);

		return metrics;
	}

	private Integer countToRoot(Scope scope) {
		int count = 0;
		while (!scope.isRoot()) {
			count++;
			scope = scope.getParent();
		}
		return count;
	}

	private Integer countToRootRelease(Release release) {
		int count = 0;
		while (!release.isRoot()) {
			count++;
			release = release.getParent();
		}
		return count;
	}

	public void exportUsageDataCsv(final OutputStream out) throws PersistenceException, IOException {
		final Map<UUID, String> projectsNamesMap = getProjectsNamesMap();

		final CsvWriter csv = new CsvWriter(out, "User", "Project", "First Action", "Last Action");
		for (final User user : persistenceService.retrieveAllUsers()) {
			final List<ProjectAuthorization> authorizations = persistenceService.retrieveProjectAuthorizations(user.getId());
			for (final ProjectAuthorization auth : authorizations) {
				final String project = projectsNamesMap.get(auth.getProjectId());
				final Date firstAction = persistenceService.retrieveFirstActionTimestamp(auth.getProjectId(), auth.getUserId());
				final Date lastAction = persistenceService.retrieveLastActionTimestamp(auth.getProjectId(), auth.getUserId());

				csv.write(user.getEmail()).and().write(project).and().write(firstAction).and().write(lastAction).closeEntry();
			}
			if (authorizations.isEmpty()) {
				csv.write(user.getEmail()).and().writeEmpty().and().writeEmpty().and().writeEmpty().closeEntry();
			}
		}
	}

	public void exportInvitationDataCsv(final ServletOutputStream out) throws PersistenceException, IOException {
		final Map<UUID, String> usersEmailsMap = getUsersEmailsMap();

		final CsvWriter csv = new CsvWriter(out, "User", "Invited User", "Project", "Timestamp");

		for (final User user : persistenceService.retrieveAllUsers()) {
			final List<UserAction> actions = persistenceService.retrieveAllTeamInviteActionsAuthoredBy(user.getId());
			for (final UserAction ua : actions) {
				final UUID invitedUserId = ua.getModelAction().getReferenceId();
				csv.write(user.getEmail()).and().write(usersEmailsMap.get(invitedUserId)).and().write(ua.getProjectRepresentation().getName()).and().write(ua.getTimestamp()).closeEntry();
			}
			if (actions.isEmpty()) {
				csv.write(user.getEmail()).and().writeEmpty().and().writeEmpty().and().writeEmpty().closeEntry();
			}
		}
	}

	private Map<UUID, String> getUsersEmailsMap() throws PersistenceException {
		final HashMap<UUID, String> map = new HashMap<UUID, String>();
		for (final User user : persistenceService.retrieveAllUsers()) {
			map.put(user.getId(), user.getEmail());
		}
		return map;
	}

	private Map<UUID, String> getProjectsNamesMap() throws PersistenceException {
		final HashMap<UUID, String> map = new HashMap<UUID, String>();
		for (final ProjectRepresentation project : persistenceService.retrieveAllProjectRepresentations()) {
			map.put(project.getId(), project.getName());
		}
		return map;
	}

	public List<UserUsageData> getUsersUsageData() {
		final ArrayList<UserUsageData> list = new ArrayList<UserUsageData>();

		try {
			for (final User user : persistenceService.retrieveAllUsers()) {
				final UserUsageData data = FACTORY.createUserUsageData().as();
				final UUID userId = user.getId();
				data.setUserEmail(user.getEmail());
				data.setUserId(user.getId().toString());
				data.setAuthorizedProjectsCount(persistenceService.retrieveProjectAuthorizations(userId).size());
				// LOGGER.info(data.getAuthorizedProjectsCount());
				data.setInvitationTimestamp(persistenceService.retrieveInvitationTimestamp(userId));
				// LOGGER.info(data.getInvitationTimestamp());
				data.setSubmittedActionsCount(persistenceService.retrieveAuthoredActionsCount(userId));
				// LOGGER.info(data.getSubmittedActionsCount());
				data.setLastActionTimestamp(persistenceService.retrieveLastActionTimestamp(userId));
				// LOGGER.info(data.getLastActionTimestamp());
				data.setInvitedUsersCount(persistenceService.retrieveAllAuthoredTeamInviteActionsCount(userId));
				// LOGGER.info(data.getInvitedUsersCount());
				list.add(data);
			}

		} catch (final PersistenceException e) {
			LOGGER.error("Failed to generate Users usage data", e);
		}
		return list;
	}
}
