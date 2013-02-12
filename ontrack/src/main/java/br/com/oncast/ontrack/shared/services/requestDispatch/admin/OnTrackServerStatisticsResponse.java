package br.com.oncast.ontrack.shared.services.requestDispatch.admin;

import java.util.Date;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class OnTrackServerStatisticsResponse implements DispatchResponse {

	private Set<UUID> onlineUsers;
	private Date timestamp;
	private int activeConnectionsCount;
	private long actionsPerHour;
	private int usersCount;
	private int projectsCount;

	public OnTrackServerStatisticsResponse() {
		this.timestamp = new Date();
	}

	public void setOnlineUsers(final Set<UUID> onlineUsers) {
		this.onlineUsers = onlineUsers;
	}

	public Set<UUID> getOnlineUsers() {
		return onlineUsers;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setActiveConnectionsCount(final int count) {
		this.activeConnectionsCount = count;
	}

	public int getActiveConnectionsCount() {
		return activeConnectionsCount;
	}

	public void setActionsPerHour(final long actionsPerHour) {
		this.actionsPerHour = actionsPerHour;
	}

	public long getActionsPerHour() {
		return this.actionsPerHour;
	}

	public int getUsersCount() {
		return this.usersCount;
	}

	public void setUsersCount(final int usersCount) {
		this.usersCount = usersCount;
	}

	public void setProjectsCount(final int projectsCount) {
		this.projectsCount = projectsCount;
	}

	public int getProjectsCount() {
		return this.projectsCount;
	}

}
