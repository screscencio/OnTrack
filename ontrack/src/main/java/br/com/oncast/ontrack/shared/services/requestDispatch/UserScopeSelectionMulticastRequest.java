package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserScopeSelectionMulticastRequest implements DispatchRequest<VoidResult> {

	private UUID selectedScopeId;

	protected UserScopeSelectionMulticastRequest() {}

	public UserScopeSelectionMulticastRequest(final UUID selectedScopeId) {
		this.selectedScopeId = selectedScopeId;
	}

	public UUID getSelectedScopeId() {
		return selectedScopeId;
	}

}
