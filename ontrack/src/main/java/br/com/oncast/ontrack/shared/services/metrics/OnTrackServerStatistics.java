package br.com.oncast.ontrack.shared.services.metrics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OnTrackServerStatistics {

	Date getTimestamp();

	void setTimestamp(final Date timestamp);

	void setActionsCount(long actionsCount);

	long getActionsCount();

	void setTotalUsersCount(int usersCount);

	int getTotalUsersCount();

	void setTotalProjectsCount(int projectsCount);

	int getTotalProjectsCount();

	void setActionsRatio(Map<String, Integer> actionsRatio);

	Map<String, Integer> getActionsRatio();

	void setActiveProjectsMetrics(List<ProjectMetrics> activeProjectsMetrics);

	List<ProjectMetrics> getActiveProjectsMetrics();

	void setUsersUsageDataList(List<UserUsageData> usersUsageData);

	List<UserUsageData> getUsersUsageDataList();

}
