package br.com.oncast.ontrack.shared.model.value;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ValueInferenceEngineScopeInfluenceTest {

	private Scope scope;

	@Before
	public void setUp() {
		scope = ScopeTestUtils.getComplexScope();
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringValueForSomeScope() throws UnableToCompleteActionException {
		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getId());
		expectedInfluencedScopes.add(scope.getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(2).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(2).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(2).getChild(1).getId());

		scope.getChild(0).getChild(2).getValue().setDeclared(30);
		assertEquals(expectedInfluencedScopes, new ValueInferenceEngine().process(scope));
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenMovingToLeft() throws UnableToCompleteActionException {
		final Scope manipulatedScope = scope.getChild(0).getChild(2);
		manipulatedScope.getValue().setDeclared(30);
		new ValueInferenceEngine().process(manipulatedScope.getParent());

		final ScopeMoveLeftAction moveLeftAction = new ScopeMoveLeftAction(manipulatedScope.getId());
		moveLeftAction.execute(ProjectTestUtils.createProjectContext(scope, null), Mockito.mock(ActionContext.class));

		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getChild(0).getId());

		assertEquals(expectedInfluencedScopes, new ValueInferenceEngine().process(scope.getChild(0)));
	}

	@Test
	public void shouldInfluenceAllScopeHierarchyWhenDeclaringValueForRootScope() throws UnableToCompleteActionException {
		final Set<UUID> influencedScopes = new HashSet<UUID>();
		influencedScopes.addAll(getAllIdsOf(scope));

		scope.getValue().setDeclared(100);
		assertEquals(influencedScopes, new ValueInferenceEngine().process(scope));
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringValueForSomeScope2() throws UnableToCompleteActionException {
		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getId());
		expectedInfluencedScopes.add(scope.getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(0).getChild(3).getId());

		scope.getChild(0).getChild(2).getValue().setDeclared(30);
		new ValueInferenceEngine().process(scope.getChild(0));

		scope.getChild(0).getValue().setDeclared(50);
		final Set<UUID> actualInfluencedScopes = new ValueInferenceEngine().process(scope);

		assertEquals(expectedInfluencedScopes, actualInfluencedScopes);
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringValueForSomeScope3() throws UnableToCompleteActionException {
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

		scope.getChild(0).getValue().setDeclared(50);
		new ValueInferenceEngine().process(scope);

		scope.getValue().setDeclared(100);
		final Set<UUID> actualInfluencedScopes = new ValueInferenceEngine().process(scope);

		assertEquals(expectedInfluencedScopes, actualInfluencedScopes);
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringValueForSomeScope4() throws UnableToCompleteActionException {
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

		scope.getChild(0).getChild(2).getValue().setDeclared(30);
		new ValueInferenceEngine().process(scope.getChild(0));

		scope.getChild(0).getValue().setDeclared(50);
		new ValueInferenceEngine().process(scope);

		scope.getValue().setDeclared(200);
		final Set<UUID> actualInfluencedScopes = new ValueInferenceEngine().process(scope);

		assertEquals(expectedInfluencedScopes, actualInfluencedScopes);
	}

	@Test
	public void shouldReturnAListWithAllInfluencedScopesWhenDeclaringValueForSomeScope5() throws UnableToCompleteActionException {
		final Set<UUID> expectedInfluencedScopes = new HashSet<UUID>();
		expectedInfluencedScopes.add(scope.getId());
		expectedInfluencedScopes.add(scope.getChild(2).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getChild(0).getId());
		expectedInfluencedScopes.add(scope.getChild(2).getChild(1).getChild(1).getId());

		scope.getValue().setDeclared(100);
		new ValueInferenceEngine().process(scope);

		scope.getChild(2).getChild(1).getChild(0).getValue().setDeclared(10);
		final Set<UUID> actualInfluencedScopes = new ValueInferenceEngine().process(scope.getChild(2).getChild(1));

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
