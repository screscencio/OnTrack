package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;

public class PasswordResetRequest implements DispatchRequest<VoidResult> {

	private String email;

	protected PasswordResetRequest() {}

	public PasswordResetRequest(final String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
