package br.com.oncast.ontrack.shared.model.scope;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class ScopeTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

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

	@Test
	public void whenScopeIsLeafGetAllLeafsShouldReturnAListContainsOnlyItself() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();
		final List<Scope> allLeafs = scope.getAllLeafs();
		Assert.assertEquals(1, allLeafs.size());

		Assert.assertTrue(allLeafs.contains(scope));
	}

	@Test
	public void whenScopeHasOneChildGetAllLeafsShouldReturnAListContainingOnlyChild() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();
		final Scope child = ScopeTestUtils.createScope();
		scope.add(child);

		final List<Scope> allLeafs = scope.getAllLeafs();
		Assert.assertEquals(1, allLeafs.size());

		Assert.assertTrue(allLeafs.contains(child));
	}

	@Test
	public void getAllLeafsShouldReturnAllLeafDescendents() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();
		final Scope child1 = ScopeTestUtils.createScope();
		final Scope child2 = ScopeTestUtils.createScope();

		final Scope grandchild = ScopeTestUtils.createScope();
		scope.add(child1);
		scope.add(child2);
		child1.add(grandchild);

		final List<Scope> allLeafs = scope.getAllLeafs();
		Assert.assertEquals(2, allLeafs.size());

		Assert.assertTrue(allLeafs.contains(grandchild));
		Assert.assertTrue(allLeafs.contains(child2));
	}

	@Test
	public void shouldNotAllowToSetReleaseWhenTheReleaseDoesNotContainsTheScope() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();
		final Release release = ReleaseTestUtils.createRelease();

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The Release should contain the scope");

		scope.setRelease(release);
	}

	@Test
	public void shouldNotSetReleaseWhenTheReleaseDoesNotContainsTheScope() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();
		final Release release = ReleaseTestUtils.createRelease();

		try {
			scope.setRelease(release);
			Assert.fail("Expected throw IllegalArgumentException");
		} catch (final IllegalArgumentException e) {
			Assert.assertNull(scope.getRelease());
		}
	}

	@Test
	public void shouldBeAbleToSetReleaseWhenTheReleaseContainsTheScope() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();
		final Release release = ReleaseTestUtils.createRelease();
		release.addScope(scope);
		assertEquals(release, scope.getRelease());
	}
}
