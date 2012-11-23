package br.com.oncast.ontrack.shared.model.effort;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class EffortInferenceEngineTest {

	private static final ProgressInferenceEngine PROGRESS_INFERENCE_ENGINE = new ProgressInferenceEngine();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();

	@Test
	public void addingAChildShouldGetParentsHoleEffort() throws Exception {
		final Scope parent = ScopeTestUtils.createScope();
		final Scope child = ScopeTestUtils.createScope();
		declare(parent, 20);
		insertChild(parent, child);

		assertInferedEffort(20, child);
	}

	private void assertInferedEffort(final float expectedEffort, final Scope scope) {
		assertEquals(expectedEffort, scope.getEffort().getInfered(), 0.1);
	}

	private Set<UUID> declare(final Scope scope, final float effort) {
		scope.getEffort().setDeclared(effort);
		return processAllInferenceEngines(scope);
	}

	private Set<UUID> insertChild(final Scope parent, final Scope child) {
		parent.add(child);
		return processAllInferenceEngines(parent);
	}

	private Set<UUID> processAllInferenceEngines(final Scope scope) {
		final Set<UUID> updatesScopes = new HashSet<UUID>();
		updatesScopes.addAll(procressEffortInference(scope));
		updatesScopes.addAll(processProgressInference(scope));
		return updatesScopes;
	}

	private Set<UUID> procressEffortInference(final Scope rootScope) {
		return EFFORT_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date(0));
	}

	private Set<UUID> processProgressInference(final Scope scope) {
		return PROGRESS_INFERENCE_ENGINE.process(scope, UserTestUtils.getAdmin(), new Date(0));
	}

}
