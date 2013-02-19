package br.com.oncast.ontrack.shared.services.requestDispatch.metrics;

import java.io.Serializable;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class OnTrackServerMetricsResponse implements DispatchResponse, Serializable {

	private static final long serialVersionUID = 1L;

	private String statistics;

	protected OnTrackServerMetricsResponse() {}

	public OnTrackServerMetricsResponse(final OnTrackServerMetrics statistics) {
		this.statistics = serialize(statistics);
	}

	public OnTrackServerMetrics getStatistics(final OnTrackStatisticsFactory factory) {
		return deserialize(this.statistics, factory);
	}

	public static String serialize(final OnTrackServerMetrics serializable) {
		final AutoBean<OnTrackServerMetrics> bean = AutoBeanUtils.getAutoBean(serializable);
		return AutoBeanCodex.encode(bean).getPayload();
	}

	public static OnTrackServerMetrics deserialize(final String json, final OnTrackStatisticsFactory factory) {
		final AutoBean<OnTrackServerMetrics> bean = AutoBeanCodex.decode(factory, OnTrackServerMetrics.class, json);
		return bean.as();
	}

}
