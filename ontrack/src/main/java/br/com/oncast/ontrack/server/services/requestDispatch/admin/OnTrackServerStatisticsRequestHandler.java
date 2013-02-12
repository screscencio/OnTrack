package br.com.oncast.ontrack.server.services.requestDispatch.admin;

import java.util.Calendar;
import java.util.Date;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsResponse;

public class OnTrackServerStatisticsRequestHandler implements RequestHandler<OnTrackServerStatisticsRequest, OnTrackServerStatisticsResponse> {

	private static final ServerServiceProvider PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public OnTrackServerStatisticsResponse handle(final OnTrackServerStatisticsRequest request) throws Exception {
		if (!DefaultAuthenticationCredentials.USER_ID.equals(PROVIDER.getAuthenticationManager().getAuthenticatedUser().getId())) throw new RuntimeException(
				"You are not admin!");

		final OnTrackServerStatisticsResponse response = new OnTrackServerStatisticsResponse();
		response.setOnlineUsers(PROVIDER.getClientManagerService().getOnlineUsers());
		response.setActiveConnectionsCount(PROVIDER.getClientManagerService().getAllClients().size());
		final Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.HOUR_OF_DAY, -1);
		response.setActionsPerHour(PROVIDER.getServerStatisticsService().getActionsCountSince(c.getTime()));
		response.setUsersCount(PROVIDER.getServerStatisticsService().getUsersCount());
		response.setProjectsCount(PROVIDER.getServerStatisticsService().getProjectsCount());
		return response;
	}
}
