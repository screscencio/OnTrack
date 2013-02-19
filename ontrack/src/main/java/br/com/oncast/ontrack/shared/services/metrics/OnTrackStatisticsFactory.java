package br.com.oncast.ontrack.shared.services.metrics;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface OnTrackStatisticsFactory extends AutoBeanFactory {

	AutoBean<OnTrackServerMetrics> createOnTrackServerMetrics();

	AutoBean<OnTrackServerMetricsBag> createOnTrackServerMetricsBag();

	AutoBean<ProjectMetrics> createProjectMetrics();

}
