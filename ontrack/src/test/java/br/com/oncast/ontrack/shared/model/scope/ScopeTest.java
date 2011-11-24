package br.com.oncast.ontrack.shared.model.scope;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ScopeTest {

	@Test
	public void shouldNotChangeChildListWhenChangingListReturnedFromGetChildrenMethod1() {
		final Scope scope = ScopeTestUtils.getComplexScope();
		final List<Scope> childrenList1 = scope.getChildren();
		childrenList1.clear();

		final List<Scope> childrenList2 = scope.getChildren();

		Assert.assertTrue(!childrenList2.equals(childrenList1));
	}

	@Test
	public void shouldNotChangeChildListWhenChangingListReturnedFromGetChildrenMethod2() {
		final Scope scope = ScopeTestUtils.getComplexScope();
		final List<Scope> childrenList1 = scope.getChildren();
		final int size = childrenList1.size();
		childrenList1.clear();

		Assert.assertEquals(0, childrenList1.size());
		Assert.assertEquals(size, scope.getChildren().size());
	}

	@Test
	public void scopeShouldFindItSelf() {
		final Scope scope = ScopeTestUtils.getComplexScope();
		final Scope foundScope = scope.findScope(scope.getId());

		Assert.assertEquals(scope, foundScope);
	}

	@Test
	public void scopeShouldFindItsDirectChild() {
		final Scope scope = ScopeTestUtils.getComplexScope();
		final Scope childScope = scope.getChild(1);
		final Scope foundScope = scope.findScope(childScope.getId());

		Assert.assertEquals(childScope, foundScope);
	}

	@Test
	public void scopeShouldFindItsGrandChild() {
		final Scope scope = ScopeTestUtils.getComplexScope();
		final Scope childScope = scope.getChild(1).getChild(0);
		final Scope foundScope = scope.findScope(childScope.getId());

		Assert.assertEquals(childScope, foundScope);
	}

	@Test
	public void scopeShouldNotFindRandomUUID() {
		final Scope scope = ScopeTestUtils.getComplexScope();
		final Scope foundScope = scope.findScope(new UUID());

		Assert.assertNull(foundScope);
	}

}
