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

	@Test
	public void testCaseWhenTheGrandChildDeclaresMoreThanTheChildsInferedEffort() throws Exception {
		final Scope root = ScopeTestUtils.createScope("root");
		final Scope grandpa1 = ScopeTestUtils.createScope("grandpa1");
		final Scope grandpa2 = ScopeTestUtils.createScope("grandpa2");
		final Scope grandpa3 = ScopeTestUtils.createScope("grandpa3");

		final Scope parent1 = ScopeTestUtils.createScope("parent1");
		final Scope parent2 = ScopeTestUtils.createScope("parent2");
		final Scope parent3 = ScopeTestUtils.createScope("parent3");

		final Scope child1 = ScopeTestUtils.createScope("child1");
		final Scope child2 = ScopeTestUtils.createScope("child2");
		final Scope child3 = ScopeTestUtils.createScope("child3");

		final Scope grandChild1 = ScopeTestUtils.createScope("grandChild1");
		final Scope grandChild2 = ScopeTestUtils.createScope("grandChild2");
		final Scope grandChild3 = ScopeTestUtils.createScope("grandChild3");

		insertChild(root, grandpa1);
		insertChild(root, grandpa2);
		insertChild(root, grandpa3);
		insertChild(grandpa1, parent1);
		insertChild(grandpa1, parent2);
		insertChild(grandpa1, parent3);
		insertChild(parent1, child1);
		insertChild(parent1, child2);
		insertChild(parent1, child3);
		insertChild(child2, grandChild1);
		insertChild(child2, grandChild2);
		insertChild(child2, grandChild3);

		final Set<UUID> inferenceInfluencedScopes = declare(grandpa1, 120);
		assertInfluenced(inferenceInfluencedScopes, grandpa1, parent1, parent2, parent3, child1, child2, child3, grandChild1, grandChild2, grandChild3);

		assertInferedEffort(40, parent1, parent2, parent3);
		assertInferedEffort(13.3, child1, child2, child3);
		assertInferedEffort(4.4, grandChild1, grandChild2, grandChild3);

		final Set<UUID> inferenceInfluencedScopes2 = declare(grandChild1, 52);
		assertInfluenced(inferenceInfluencedScopes2, parent1, parent2, parent3, child1, child2, child3, grandChild1, grandChild2, grandChild3);

		assertInferedEffort(52, grandChild1, child2, parent1);
		assertInferedEffort(34, parent2, parent3);
		assertInferedEffort(0, grandChild2, grandChild3, child1, child3);
		assertInferedEffort(120, grandpa1);
	}

	private void assertInfluenced(final Set<UUID> inferenceInfluencedScopes, final Scope... scopes) {
		for (final Scope scope : scopes) {
			assertTrue("Expected " + scope.getDescription() + " to be included in the influencedScopesSet", inferenceInfluencedScopes.contains(scope));
		}
	}

	private void assertInferedEffort(final double expectedEffort, final Scope... scopes) {
		for (final Scope scope : scopes) {
			assertEquals(expectedEffort, scope.getEffort().getInfered(), 0.1);
		}
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

	private Set<UUID> procressEffortInference(final Scope scope) {
		return EFFORT_INFERENCE_ENGINE.process(scope, UserRepresentationTestUtils.getAdmin(), new Date(0));
	}

	private Set<UUID> processProgressInference(final Scope scope) {
		return PROGRESS_INFERENCE_ENGINE.process(scope, UserRepresentationTestUtils.getAdmin(), new Date(0));
	}

}
