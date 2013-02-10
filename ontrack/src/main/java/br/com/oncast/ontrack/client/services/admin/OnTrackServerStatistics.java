package br.com.oncast.ontrack.client.services.admin;

import java.util.Date;
import java.util.Set;

public interface OnTrackServerStatistics {

	Date getTimestamp();

	void setTimestamp(final Date timestamp);

	Set<String> getOnlineUsers();

	void setOnlineUsers(final Set<String> onlineUsers);

}
