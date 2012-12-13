package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.ArrayList;
import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserDataRequest implements DispatchRequest<UserDataRequestResponse> {

	private List<UUID> usersList;

	protected UserDataRequest() {}

	public UserDataRequest(final List<UserRepresentation> list) {
		this.usersList = new ArrayList<UUID>();
		for (final UserRepresentation userRepresentation : list) {
			usersList.add(userRepresentation.getId());
		}
	}

	public UserDataRequest(final UUID... userIds) {
		this.usersList = new ArrayList<UUID>();
		for (final UUID id : userIds) {
			this.usersList.add(id);
		}
	}

	public List<UUID> getUsersList() {
		return usersList;
	}
}
