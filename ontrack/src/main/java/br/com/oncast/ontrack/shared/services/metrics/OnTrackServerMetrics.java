package br.com.oncast.ontrack.shared.services.metrics;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OnTrackServerMetrics {

	Date getTimestamp();

	void setTimestamp(final Date timestamp);

	Set<String> getOnlineUsers();

	void setOnlineUsers(final Set<String> onlineUsers);

	void setActiveConnectionsCount(int activeConnectionsCount);

	int getActiveConnectionsCount();

	void setActionsCount(long actionsCount);

	long getActionsCount();

	void setUsersCount(int usersCount);

	int getUsersCount();

	void setProjectsCount(int projectsCount);

	int getProjectsCount();

	void setActionsRatio(Map<String, Integer> actionsRatio);

	Map<String, Integer> getActionsRatio();

	void setActiveProjectsMetrics(List<ProjectMetrics> activeProjectsMetrics);

	List<ProjectMetrics> getActiveProjectsMetrics();

}
