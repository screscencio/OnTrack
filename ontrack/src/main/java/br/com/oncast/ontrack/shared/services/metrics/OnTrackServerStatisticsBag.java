package br.com.oncast.ontrack.shared.services.metrics;

import java.util.List;

public interface OnTrackServerStatisticsBag {

	List<OnTrackServerStatistics> getOnTrackServerStatisticsList();

	void setOnTrackServerStatisticsList(final List<OnTrackServerStatistics> statisticsList);

}
