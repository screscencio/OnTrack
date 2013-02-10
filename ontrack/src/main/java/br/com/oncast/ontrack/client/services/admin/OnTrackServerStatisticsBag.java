package br.com.oncast.ontrack.client.services.admin;

import java.util.List;

public interface OnTrackServerStatisticsBag {

	List<OnTrackServerStatistics> getStatisticsList();

	void setStatisticsList(final List<OnTrackServerStatistics> statisticsList);

}
