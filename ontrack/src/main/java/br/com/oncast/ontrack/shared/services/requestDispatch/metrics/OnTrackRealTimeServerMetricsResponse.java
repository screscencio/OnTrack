package br.com.oncast.ontrack.shared.services.requestDispatch.metrics;

import java.io.Serializable;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class OnTrackRealTimeServerMetricsResponse implements DispatchResponse, Serializable {

	private static final long serialVersionUID = 1L;

	private String statistics;

	protected OnTrackRealTimeServerMetricsResponse() {}

	public OnTrackRealTimeServerMetricsResponse(final OnTrackRealTimeServerMetrics statistics) {
		this.statistics = serialize(statistics);
	}

	public OnTrackRealTimeServerMetrics getStatistics(final OnTrackStatisticsFactory factory) {
		return deserialize(this.statistics, factory);
	}

	public static String serialize(final OnTrackRealTimeServerMetrics serializable) {
		final AutoBean<OnTrackRealTimeServerMetrics> bean = AutoBeanUtils.getAutoBean(serializable);
		return AutoBeanCodex.encode(bean).getPayload();
	}

	public static OnTrackRealTimeServerMetrics deserialize(final String json, final OnTrackStatisticsFactory factory) {
		final AutoBean<OnTrackRealTimeServerMetrics> bean = AutoBeanCodex.decode(factory, OnTrackRealTimeServerMetrics.class, json);
		return bean.as();
	}

}
