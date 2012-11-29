package br.com.oncast.ontrack.shared.model.scope.inference;

import java.util.Date;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface InferenceOverScopeEngine {

	public boolean shouldProcess(final ScopeAction action);

	public Set<UUID> process(final Scope scope, UserRepresentation author, Date timestamp);
}
