package br.com.oncast.ontrack.utils.inference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

import com.google.common.base.Joiner;

public class InferenceEngineTestUtils {

	private static final ProgressInferenceEngine PROGRESS_INFERENCE_ENGINE = new ProgressInferenceEngine();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();
	private static Map<UUID, Scope> scopesCache = new HashMap<UUID, Scope>();

	// Declarations

	public static Set<UUID> declare(final Scope scope, final ProgressState state) {
		addToCache(scope);
		ScopeTestUtils.declareProgress(scope, state);
		return processAllInferenceEngines(scope);
	}

	public static Set<UUID> declare(final Scope scope, final double effort) {
		addToCache(scope);
		scope.getEffort().setDeclared((float) effort);
		return processAllInferenceEngines(scope);
	}

	// Scope Insertions

	public static Set<UUID> insertSibling(final Scope sibling, final Scope newScope) {
		assert !sibling.isRoot();
		addToCache(sibling, newScope);

		sibling.getParent().add(newScope);
		return processAllInferenceEngines(sibling);
	}

	public static Set<UUID> insertChild(final Scope parent, final Scope newScope) {
		addToCache(parent, newScope);
		parent.add(newScope);
		return processAllInferenceEngines(parent);
	}

	// Scope Removal

	public static Set<UUID> removeScope(final Scope scope) {
		assert !scope.isRoot();
		addToCache(scope);

		final Scope parent = scope.getParent();
		parent.remove(scope);
		return processAllInferenceEngines(parent);
	}

	// Processments

	public static Set<UUID> processAllInferenceEngines(final Scope scope) {
		addToCache(scope);
		final Set<UUID> updatesScopes = new HashSet<UUID>();
		updatesScopes.addAll(procressEffortInference(scope));
		updatesScopes.addAll(processProgressInference(scope));
		return updatesScopes;
	}

	public static Set<UUID> procressEffortInference(final Scope scope) {
		return EFFORT_INFERENCE_ENGINE.process(scope, UserTestUtils.getAdmin(), new Date(0));
	}

	public static Set<UUID> processProgressInference(final Scope scope) {
		return PROGRESS_INFERENCE_ENGINE.process(scope, UserTestUtils.getAdmin(), new Date(0));
	}

	// Assertions

	public static void assertUpdatedScopes(final Set<UUID> updatedScopes, final Scope... scopes) {
		assertUpdatedScopes(updatedScopes, Arrays.asList(scopes));
	}

	public static void assertUpdatedScopes(final Set<UUID> updatedScopes, final Collection<Scope> expectedScopes, final Scope... otherExpectedScopes) {
		final HashSet<UUID> actual = new HashSet<UUID>(updatedScopes);
		final HashSet<Scope> expected = new HashSet<Scope>(Arrays.asList(otherExpectedScopes));
		expected.addAll(expectedScopes);

		for (final Scope scope : expected) {
			assertTrue(scope.getDescription() + " missing", actual.remove(scope.getId()));
		}
		final HashSet<String> exceededScopes = new HashSet<String>();
		for (final UUID id : actual) {
			exceededScopes.add(scopesCache.containsKey(id) ? scopesCache.get(id).getDescription() : id.toStringRepresentation());
		}
		assertTrue("There are " + actual.size() + " more scopes than expected: " + Joiner.on(", ").join(exceededScopes),
				actual.isEmpty());
	}

	public static void assertInferedEffort(final double expectedEffort, final Scope scope) {
		addToCache(scope);
		assertEquals(expectedEffort, scope.getEffort().getInfered(), 0.1);
	}

	public static void assertAccomplishedEffort(final double expectedEffort, final Scope scope) {
		assertEquals(expectedEffort, scope.getEffort().getAccomplished(), 0.1);
	}

	public static void assertProgress(final ProgressState expectedState, final Scope scope) {
		assertEquals(expectedState, scope.getProgress().getState());
	}

	private static void addToCache(final Scope... scopes) {
		for (final Scope scope : scopes) {
			scopesCache.put(scope.getId(), scope);
		}
	}

}
