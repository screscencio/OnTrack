package br.com.oncast.ontrack.shared.model.scope;

import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;

public class InferenceEngineTest {

	@Test
	public void shouldChangeParentsEffortIfTheirEffortIsDifferent() {
		final int newEffort = 0;
		final Scope root = ScopeMock.getScope();
		final Scope changed = root.getChild(0).getChild(1);

		EffortInferenceEngine.updateModel(changed, 15);
	}
}
