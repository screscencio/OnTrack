package br.com.oncast.ontrack.shared.model.effort;

import br.com.oncast.ontrack.shared.model.prioritizationCriteria.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void declaringEffortInParentAndDeclaringEffortInAChildShouldReDistributeTheEffortAmongTheChildren() throws Exception {
		final Scope root = ScopeTestUtils.createScope();
		final Scope parent = ScopeTestUtils.createScope();
		final Scope child1 = ScopeTestUtils.createScope();
		final Scope child2 = ScopeTestUtils.createScope();
		final Scope child3 = ScopeTestUtils.createScope();
		insertChild(root, parent);
		insertChild(parent, child1);
		insertChild(parent, child2);
		insertChild(parent, child3);

		declare(parent, 9);

		assertInferedEffort(3, child1);
		assertInferedEffort(3, child2);
		assertInferedEffort(3, child3);

		final Set<UUID> inferenceInfluencedScopes = declare(child1, 5);

		assertInferedEffort(2, child2);
		assertInferedEffort(2, child3);

		assertTrue(inferenceInfluencedScopes.contains(child2));
		assertTrue(inferenceInfluencedScopes.contains(child3));
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
		return EFFORT_INFERENCE_ENGINE.process(rootScope, UserRepresentationTestUtils.getAdmin(), new Date(0));
	}

	private Set<UUID> processProgressInference(final Scope scope) {
		return PROGRESS_INFERENCE_ENGINE.process(scope, UserRepresentationTestUtils.getAdmin(), new Date(0));
	}

}
