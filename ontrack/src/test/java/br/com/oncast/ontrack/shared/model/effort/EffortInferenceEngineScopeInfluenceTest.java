package br.com.oncast.ontrack.shared.model.effort;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class EffortInferenceEngineScopeInfluenceTest {

	private Scope scope;

	@Before
	public void setUp() {
		scope = ScopeMock.getComplexScope();
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringEffortForSomeScope() throws UnableToCompleteActionException {
		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getId());
		expectedInfluencedScopes.add(scope.getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(2).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(2).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(2).getChild(1).getId());

		scope.getChild(0).getChild(2).getEffort().setDeclared(30);
		assertEquals(expectedInfluencedScopes, new EffortInferenceEngine().process(scope));
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenMovingToLeft() throws UnableToCompleteActionException {
		final Scope manipulatedScope = scope.getChild(0).getChild(2);
		manipulatedScope.getEffort().setDeclared(30);
		new EffortInferenceEngine().process(manipulatedScope.getParent());

		final ScopeMoveLeftAction moveLeftAction = new ScopeMoveLeftAction(manipulatedScope.getId());
		moveLeftAction.execute(new ProjectContext(new Project(scope, null)));

		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getChild(0).getId());

		assertEquals(expectedInfluencedScopes, new EffortInferenceEngine().process(scope.getChild(0)));
	}

	@Test
	public void shouldInfluenceAllScopeHierarchyWhenDeclaringEffortForRootScope() throws UnableToCompleteActionException {
		final Set<UUID> influencedScopes = new HashSet<UUID>();
		influencedScopes.addAll(getAllIdsOf(scope));

		scope.getEffort().setDeclared(100);
		assertEquals(influencedScopes, new EffortInferenceEngine().process(scope));
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringEffortForSomeScope2() throws UnableToCompleteActionException {
		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getId());
		expectedInfluencedScopes.add(scope.getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(3).getId());

		scope.getChild(0).getChild(2).getEffort().setDeclared(30);
		new EffortInferenceEngine().process(scope.getChild(0));

		scope.getChild(0).getEffort().setDeclared(50);
		final Set<UUID> actualInfluencedScopes = new EffortInferenceEngine().process(scope);

		assertEquals(expectedInfluencedScopes, actualInfluencedScopes);
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringEffortForSomeScope3() throws UnableToCompleteActionException {
		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getId());
		expectedInfluencedScopes.add(scope.getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(1).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(3).getId());

		scope.getChild(0).getEffort().setDeclared(50);
		new EffortInferenceEngine().process(scope);

		scope.getEffort().setDeclared(100);
		final Set<UUID> actualInfluencedScopes = new EffortInferenceEngine().process(scope);

		assertEquals(expectedInfluencedScopes, actualInfluencedScopes);
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringEffortForSomeScope4() throws UnableToCompleteActionException {
		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getId());
		expectedInfluencedScopes.add(scope.getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(1).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(3).getId());

		scope.getChild(0).getChild(2).getEffort().setDeclared(30);
		new EffortInferenceEngine().process(scope.getChild(0));

		scope.getChild(0).getEffort().setDeclared(50);
		new EffortInferenceEngine().process(scope);

		scope.getEffort().setDeclared(200);
		final Set<UUID> actualInfluencedScopes = new EffortInferenceEngine().process(scope);

		assertEquals(expectedInfluencedScopes, actualInfluencedScopes);
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringEffortForSomeScope5() throws UnableToCompleteActionException {
		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getId());
		expectedInfluencedScopes.add(scope.getChild(2).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getChild(1).getId());

		scope.getEffort().setDeclared(100);
		new EffortInferenceEngine().process(scope);

		scope.getChild(2).getChild(1).getChild(0).getEffort().setDeclared(10);
		final Set<UUID> actualInfluencedScopes = new EffortInferenceEngine().process(scope.getChild(2).getChild(1));

		assertEquals(expectedInfluencedScopes, actualInfluencedScopes);
	}

	private Set<UUID> getAllIdsOf(final Scope scopeHierarchy) {
		final Set<UUID> ids = new HashSet<UUID>();

		ids.add(scopeHierarchy.getId());
		for (final Scope child : scopeHierarchy.getChildren()) {
			ids.addAll(getAllIdsOf(child));
		}
		return ids;
	}

}
