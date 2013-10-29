package br.com.oncast.ontrack.shared.services.metrics;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface OnTrackStatisticsFactory extends AutoBeanFactory {

	AutoBean<OnTrackRealTimeServerMetrics> createOnTrackRealTimeServerMetrics();

	AutoBean<OnTrackRealTimeServerMetricsBag> createOnTrackRealTimeServerMetricsBag();

	AutoBean<OnTrackServerStatistics> createOnTrackServerStatistics();

	AutoBean<OnTrackServerStatisticsBag> createOnTrackServerStatisticsBag();

	AutoBean<ProjectMetrics> createProjectMetrics();

	AutoBean<UserUsageData> createUserUsageData();

}
