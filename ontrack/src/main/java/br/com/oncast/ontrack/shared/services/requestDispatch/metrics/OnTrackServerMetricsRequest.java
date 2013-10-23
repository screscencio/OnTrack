package br.com.oncast.ontrack.shared.services.requestDispatch.metrics;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import java.util.Date;

public class OnTrackServerMetricsRequest implements DispatchRequest<OnTrackServerMetricsResponse> {

	private Date dateOfLastMetricsRequest;

	protected OnTrackServerMetricsRequest() {}

	public OnTrackServerMetricsRequest(final Date dateOfLastRequest) {
		this.dateOfLastMetricsRequest = dateOfLastRequest;
	}

	public Date getDateOfLastMetricsRequest() {
		return dateOfLastMetricsRequest;
	}

}
