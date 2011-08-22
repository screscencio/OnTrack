package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ModelActionSyncRequest extends ProjectContextRequest {

	private static final long serialVersionUID = 1L;

	private UUID id;
	private ModelAction action;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ModelActionSyncRequest() {}

	public ModelActionSyncRequest(final ModelAction action) {
		id = new UUID();
		this.action = action;
	}

	public UUID getId() {
		return id;
	}

	public ModelAction getAction() {
		return action;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ModelActionSyncRequest)) return false;
		final ModelActionSyncRequest other = (ModelActionSyncRequest) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

}
