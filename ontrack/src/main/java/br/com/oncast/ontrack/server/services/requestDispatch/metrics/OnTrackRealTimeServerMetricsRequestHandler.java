package br.com.oncast.ontrack.server.services.requestDispatch.metrics;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackRealTimeServerMetricsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackRealTimeServerMetricsResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

public class OnTrackRealTimeServerMetricsRequestHandler implements RequestHandler<OnTrackRealTimeServerMetricsRequest, OnTrackRealTimeServerMetricsResponse> {

	private static final ServerServiceProvider PROVIDER = ServerServiceProvider.getInstance();
	private static final OnTrackStatisticsFactory FACTORY = AutoBeanFactorySource.create(OnTrackStatisticsFactory.class);
	private static final Calendar CALENDAR = Calendar.getInstance();

	@Override
	public OnTrackRealTimeServerMetricsResponse handle(final OnTrackRealTimeServerMetricsRequest request) throws Exception {
		if (!DefaultAuthenticationCredentials.USER_ID.equals(PROVIDER.getAuthenticationManager().getAuthenticatedUser().getId())) throw new RuntimeException("You are not admin!");

		CALENDAR.setTime(new Date());
		final OnTrackRealTimeServerMetrics metrics = FACTORY.createOnTrackRealTimeServerMetrics().as();
		metrics.setTimestamp(CALENDAR.getTime());

		metrics.setOnlineUsers(toStringSet(PROVIDER.getClientManagerService().getOnlineUsers()));
		metrics.setActiveConnectionsCount(PROVIDER.getClientManagerService().getAllClients().size());

		final Date lastRequest = request.getDateOfLastMetricsRequest();
		CALENDAR.add(Calendar.HOUR_OF_DAY, -1);
		metrics.setActionsCount(PROVIDER.getServerMetricsService().getActionsCountSince(lastRequest == null ? CALENDAR.getTime() : lastRequest));

		return new OnTrackRealTimeServerMetricsResponse(metrics);
	}

	private Set<String> toStringSet(final Set<UUID> uuidSet) {
		final Set<String> stringSet = new HashSet<String>();
		for (final UUID id : uuidSet) {
			stringSet.add(id.toString());
		}
		return stringSet;
	}
}
