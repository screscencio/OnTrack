package br.com.oncast.ontrack.shared.services.requestDispatch.metrics;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatistics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;

import java.io.Serializable;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class OnTrackServerStatisticsResponse implements DispatchResponse, Serializable {

	private static final long serialVersionUID = 1L;

	private String statistics;

	protected OnTrackServerStatisticsResponse() {}

	public OnTrackServerStatisticsResponse(final OnTrackServerStatistics statistics) {
		this.statistics = serialize(statistics);
	}

	public OnTrackServerStatistics getStatistics(final OnTrackStatisticsFactory factory) {
		return deserialize(this.statistics, factory);
	}

	public static String serialize(final OnTrackServerStatistics serializable) {
		final AutoBean<OnTrackServerStatistics> bean = AutoBeanUtils.getAutoBean(serializable);
		return AutoBeanCodex.encode(bean).getPayload();
	}

	public static OnTrackServerStatistics deserialize(final String json, final OnTrackStatisticsFactory factory) {
		final AutoBean<OnTrackServerStatistics> bean = AutoBeanCodex.decode(factory, OnTrackServerStatistics.class, json);
		return bean.as();
	}

}
