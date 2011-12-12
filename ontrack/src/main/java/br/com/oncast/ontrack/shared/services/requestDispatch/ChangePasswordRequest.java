package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;

public class ChangePasswordRequest implements DispatchRequest<VoidResult> {

	private String currentPassword;
	private String newPassword;

	protected ChangePasswordRequest() {}

	public ChangePasswordRequest(final String currentPassword, final String newPassword) {
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

}
