package br.com.oncast.ontrack.shared.services.metrics;

import java.util.Date;
import java.util.Set;

public interface OnTrackRealTimeServerMetrics {

	Date getTimestamp();

	void setTimestamp(final Date timestamp);

	Set<String> getOnlineUsers();

	void setOnlineUsers(final Set<String> onlineUsers);

	void setActiveConnectionsCount(int activeConnectionsCount);

	int getActiveConnectionsCount();

	void setActionsCount(long actionsCount);

	long getActionsCount();

}
