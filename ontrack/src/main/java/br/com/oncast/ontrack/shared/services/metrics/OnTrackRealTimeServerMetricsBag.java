package br.com.oncast.ontrack.shared.services.metrics;

import java.util.List;

public interface OnTrackRealTimeServerMetricsBag {

	List<OnTrackRealTimeServerMetrics> getOnTrackRealTimeServerMetricsList();

	void setOnTrackRealTimeServerMetricsList(final List<OnTrackRealTimeServerMetrics> statisticsList);

}
