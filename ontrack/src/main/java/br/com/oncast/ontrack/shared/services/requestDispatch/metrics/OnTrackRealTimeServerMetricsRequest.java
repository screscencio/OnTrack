package br.com.oncast.ontrack.shared.services.requestDispatch.metrics;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import java.util.Date;

public class OnTrackRealTimeServerMetricsRequest implements DispatchRequest<OnTrackRealTimeServerMetricsResponse> {

	private Date dateOfLastMetricsRequest;

	protected OnTrackRealTimeServerMetricsRequest() {}

	public OnTrackRealTimeServerMetricsRequest(final Date dateOfLastRequest) {
		this.dateOfLastMetricsRequest = dateOfLastRequest;
	}

	public Date getDateOfLastMetricsRequest() {
		return dateOfLastMetricsRequest;
	}

}
