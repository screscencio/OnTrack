package br.com.oncast.ontrack.shared.services.metrics;

import java.util.List;

public interface OnTrackServerMetricsBag {

	List<OnTrackServerMetrics> getStatisticsList();

	void setStatisticsList(final List<OnTrackServerMetrics> statisticsList);

}
