package br.com.oncast.ontrack.shared.services.requestDispatch.admin;

import java.util.Date;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class OnTrackServerStatisticsResponse implements DispatchResponse {

	private Set<UUID> onlineUsers;
	private Date timestamp;

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

}
