package br.com.oncast.ontrack.client.services.actionSync;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ActionSyncEntry implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1L;

	private ModelAction action;

	private ActionContext context;

	private ModelAction reverseAction;

	protected ActionSyncEntry() {}

	public ActionSyncEntry(final ModelAction action, final ModelAction reverseAction, final ActionContext context) {
		this.action = action;
		this.context = context;
		this.reverseAction = reverseAction;
	}

	public ModelAction getAction() {
		return action;
	}

	public ActionContext getContext() {
		return context;
	}

	public ModelAction getReverseAction() {
		return reverseAction;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ActionSyncEntry other = (ActionSyncEntry) obj;
		if (action == null) {
			if (other.action != null) return false;
		} else if (!action.equals(other.action)) return false;
		return true;
	}

}