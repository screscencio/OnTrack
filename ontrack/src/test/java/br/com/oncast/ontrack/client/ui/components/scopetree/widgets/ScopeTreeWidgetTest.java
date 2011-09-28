package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.octo.gwt.test.GwtTest;

public class ScopeTreeWidgetTest extends GwtTest {

	private ScopeTreeWidget scopeTreeWidget;
	private Scope scopeA1;
	private Scope scopeA11;
	private Scope scopeA12;
	private Scope scopeA13;

	@Before
	public void setUp() {
		scopeTreeWidget = new ScopeTreeWidget(new ScopeTreeWidgetInteractionHandler() {

			@Override
			public void onItemEditionCancel() {}

			@Override
			public void onItemEditionEnd(final ScopeTreeItem item, final String value) {}

			@Override
			public void onKeyUp(final KeyUpEvent event) {}

			@Override
			public void onBindReleaseRequest(final UUID scopeId, final String releaseDescription) {}

			@Override
			public void onDeclareProgressRequest(final UUID scopeId, final String progressDescription) {}

			@Override
			public void onItemEditionStart(final ScopeTreeItem item) {}
		});
		scopeA1 = new Scope("root");
		scopeA11 = new Scope("A11");
		scopeA12 = new Scope("A12");
		scopeA13 = new Scope("A13");
		scopeA1.add(scopeA11);
		scopeA1.add(scopeA12);
	}

	@Test(expected = ScopeNotFoundException.class)
	public void shouldNotFindItemInCache() throws ScopeNotFoundException {
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
	}

	@Test(expected = ScopeNotFoundException.class)
	public void shouldNotFindItemInCache2() throws ScopeNotFoundException {
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
	}

	@Test(expected = ScopeNotFoundException.class)
	public void shouldNotFindItemInCache3() throws ScopeNotFoundException {
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
	}

	@Test(expected = ScopeNotFoundException.class)
	public void shouldNotFindItemInCache4() throws ScopeNotFoundException {
		scopeTreeWidget.findScopeTreeItem(scopeA13.getId());
	}

	@Test(expected = ScopeNotFoundException.class)
	public void shouldNotFindItemInCache5() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
			scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
			scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
		}
		catch (final Exception e) {}
		scopeTreeWidget.findScopeTreeItem(scopeA13.getId());
	}

	@Test
	public void shouldAddNewItemToCache1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
	}

	@Test
	public void shouldAddNewItemsToCache1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem1 = new ScopeTreeItem(scopeA1);
		final ScopeTreeItem treeItem2 = new ScopeTreeItem(scopeA13);
		scopeTreeWidget.add(treeItem1);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.add(treeItem2);
		scopeTreeWidget.findScopeTreeItem(scopeA13.getId());
	}

	@Test
	public void shouldAddNewItemsToCache2() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem1 = new ScopeTreeItem(scopeA1);
		final ScopeTreeItem treeItem2 = new ScopeTreeItem(scopeA13);
		scopeTreeWidget.add(treeItem1);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		treeItem1.addItem(treeItem2);
		scopeTreeWidget.findScopeTreeItem(scopeA13.getId());
	}

	@Test
	public void shouldAddNewItemsToCache3() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem1 = new ScopeTreeItem(scopeA1);
		final ScopeTreeItem treeItem2 = new ScopeTreeItem(scopeA13);
		scopeTreeWidget.add(treeItem1);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		treeItem1.insertItem(0, treeItem2);
		scopeTreeWidget.findScopeTreeItem(scopeA13.getId());
	}

	@Test
	public void shouldAddNewItemAndItsChildrenToCache() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
	}

	@Test
	public void shouldEmptCacheWhenCleaningTree1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
		scopeTreeWidget.clear();
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
	}

	@Test
	public void shouldRemoveFromCacheWhenRemovingFromParentItem1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
		treeItem.remove();
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
	}

	@Test
	public void shouldRemoveFromCacheWhenRemovingFromParentItem2() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
		treeItem.removeItems();
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
	}

	@Test
	public void shouldRemoveFromCacheWhenRemovingFromParentItem3() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		final ScopeTreeItem treeItemA11 = scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
		treeItem.removeItem(treeItemA11);
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
	}

	@Test
	public void shouldRemoveFromCacheWhenRemovingFromParentItem4() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
		final ScopeTreeItem treeItemA12 = scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
		treeItem.removeItem(treeItemA12);
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
	}

	@Test
	public void shouldAddNewItemToCache2() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(0, treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
	}

	@Test(expected = ScopeNotFoundException.class)
	public void shouldAddAndThenRemoveNewItemFromCache1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.remove(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
	}

	@Test(expected = ScopeNotFoundException.class)
	public void shouldAddAndThenRemoveNewItemFromCache2() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.remove(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
	}

	@Test(expected = ScopeNotFoundException.class)
	public void shouldAddAndThenRemoveNewItemFromCache3() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.remove(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
	}

	@Test
	public void shouldAddAndThenRemoveNewItemFromCache4() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.remove(treeItem);
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
	}

	@Test
	public void shouldAddAndThenRemoveNewItemAndItsChildrenFromCache() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
		scopeTreeWidget.remove(treeItem);
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
		try {
			scopeTreeWidget.findScopeTreeItem(scopeA12.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
	}

	@Test
	public void shouldAddItemAndThenRemoveChildItemFromCache1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);

		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		final ScopeTreeItem childTreeItem = scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());

		treeItem.removeItem(childTreeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1.getId());
		scopeTreeWidget.findScopeTreeItem(scopeA12.getId());

		try {
			scopeTreeWidget.findScopeTreeItem(scopeA11.getId());
			Assert.fail("An exception should have be thrown.");
		}
		catch (final Exception e) {
			// Purposefuly ignoring exception.
		}
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}
}
