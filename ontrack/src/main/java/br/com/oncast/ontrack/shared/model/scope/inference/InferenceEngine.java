package br.com.oncast.ontrack.shared.model.scope.inference;

import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface InferenceEngine {

	public boolean shouldProcess(final ModelAction action);

	public Set<UUID> process(final Scope scope);
}
