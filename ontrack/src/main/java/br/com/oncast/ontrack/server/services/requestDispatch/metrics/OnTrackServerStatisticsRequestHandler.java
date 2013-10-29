package br.com.oncast.ontrack.server.services.requestDispatch.metrics;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatistics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackServerStatisticsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackServerStatisticsResponse;

import java.util.Calendar;
import java.util.Date;

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

public class OnTrackServerStatisticsRequestHandler implements RequestHandler<OnTrackServerStatisticsRequest, OnTrackServerStatisticsResponse> {

	private static final ServerServiceProvider PROVIDER = ServerServiceProvider.getInstance();
	private static final OnTrackStatisticsFactory FACTORY = AutoBeanFactorySource.create(OnTrackStatisticsFactory.class);
	private static final Calendar CALENDAR = Calendar.getInstance();

	@Override
	public OnTrackServerStatisticsResponse handle(final OnTrackServerStatisticsRequest request) throws Exception {
		if (!DefaultAuthenticationCredentials.USER_ID.equals(PROVIDER.getAuthenticationManager().getAuthenticatedUser().getId())) throw new RuntimeException("You are not admin!");

		CALENDAR.setTime(new Date());
		final OnTrackServerStatistics metrics = FACTORY.createOnTrackServerStatistics().as();
		metrics.setTimestamp(CALENDAR.getTime());

		metrics.setActionsCount(PROVIDER.getServerMetricsService().getActionsCountSince(ago(Calendar.DAY_OF_MONTH, 1)));

		metrics.setActionsRatio(PROVIDER.getServerMetricsService().getActionsRatio(ago(Calendar.MONTH, 1)));

		metrics.setActiveProjectsMetrics(PROVIDER.getServerMetricsService().getActiveProjectsMetrics());

		metrics.setTotalUsersCount(PROVIDER.getServerMetricsService().getTotalUsersCount());
		metrics.setTotalProjectsCount(PROVIDER.getServerMetricsService().getTotalProjectsCount());

		metrics.setUsersUsageDataList(PROVIDER.getServerMetricsService().getUsersUsageData());

		return new OnTrackServerStatisticsResponse(metrics);
	}

	private Date ago(final int calendarField, final int amount) {
		CALENDAR.add(calendarField, -amount);
		final Date date = CALENDAR.getTime();
		CALENDAR.add(calendarField, amount);
		return date;
	}

}
