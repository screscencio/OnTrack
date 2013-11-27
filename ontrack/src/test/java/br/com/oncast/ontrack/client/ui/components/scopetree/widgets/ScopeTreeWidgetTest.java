package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import br.com.oncast.ontrack.client.services.ClientServicesTestUtils;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;

@GwtModule("br.com.oncast.ontrack.Application")
public class ScopeTreeWidgetTest extends GwtTest {

	private ScopeTreeWidget scopeTreeWidget;
	private Scope scopeA1;
	private Scope scopeA11;
	private Scope scopeA12;
	private Scope scopeA13;

	@BeforeClass
	public static void beforeClass() throws Exception {
		ClientServicesTestUtils.configure().mockEssential();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		ClientServicesTestUtils.reset();
	}

	@Before
	public void setUp() {
		scopeTreeWidget = new ScopeTreeWidget(mock(ScopeTreeWidgetInteractionHandler.class));
		scopeA1 = ScopeTestUtils.createScope("root");
		scopeA11 = ScopeTestUtils.createScope("A11");
		scopeA12 = ScopeTestUtils.createScope("A12");
		scopeA13 = ScopeTestUtils.createScope("A13");
		scopeA1.add(scopeA11);
		scopeA1.add(scopeA12);
	}

	@After
	public void cleanUp() {
		getBrowserSimulator().fireLoopEnd();
	}

	@Test
	public void shouldNotFindItemInCache() throws ScopeNotFoundException {
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA1).isFake());
	}

	@Test
	public void shouldNotFindItemInCache2() throws ScopeNotFoundException {
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA11).isFake());
	}

	@Test
	public void shouldNotFindItemInCache3() throws ScopeNotFoundException {
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA12).isFake());
	}

	@Test
	public void shouldNotFindItemInCache4() throws ScopeNotFoundException {
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA13).isFake());
	}

	@Test
	public void shouldAddFakeItemRepresentingChildrem() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		assertFalse(scopeTreeWidget.findScopeTreeItem(scopeA1).isFake());
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA11).isFake());
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA12).isFake());
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA13).isFake());
	}

	@Test
	public void shouldAddNewItemToCache1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
	}

	@Test
	public void shouldAddNewItemsToCache1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem1 = new ScopeTreeItem(scopeA1);
		final ScopeTreeItem treeItem2 = new ScopeTreeItem(scopeA13);
		scopeTreeWidget.add(treeItem1);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem2);
		scopeTreeWidget.findScopeTreeItem(scopeA13);
	}

	@Test
	public void shouldAddNewItemsToCache2() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem1 = new ScopeTreeItem(scopeA1);
		final ScopeTreeItem treeItem2 = new ScopeTreeItem(scopeA13);
		scopeTreeWidget.add(treeItem1);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
		treeItem1.addItem(treeItem2);
		scopeTreeWidget.findScopeTreeItem(scopeA13);
	}

	@Test
	public void shouldAddNewItemsToCache3() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem1 = new ScopeTreeItem(scopeA1);
		final ScopeTreeItem treeItem2 = new ScopeTreeItem(scopeA13);
		scopeTreeWidget.add(treeItem1);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
		treeItem1.insertItem(0, treeItem2);
		scopeTreeWidget.findScopeTreeItem(scopeA13);
	}

	@Test
	public void shouldAddNewItemAndItsChildrenToCache() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
		scopeTreeWidget.findScopeTreeItem(scopeA11);
		scopeTreeWidget.findScopeTreeItem(scopeA12);
	}

	@Test
	public void shouldEmptCacheWhenCleaningTree1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
		scopeTreeWidget.findScopeTreeItem(scopeA11);
		scopeTreeWidget.findScopeTreeItem(scopeA12);
		scopeTreeWidget.clear();
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA1).isFake());
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA11).isFake());
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA12).isFake());
	}

	@Test
	public void shouldRemoveFromCacheWhenRemovingFromTree() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
		scopeTreeWidget.findScopeTreeItem(scopeA11);
		scopeTreeWidget.findScopeTreeItem(scopeA12);
		treeItem.remove();
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA11).isFake());
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA12).isFake());
	}

	@Test
	public void shouldAddNewItemToCache2() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(0, treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
	}

	@Test
	public void shouldAddAndThenRemoveNewItemFromCache1() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.remove(treeItem);
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA1).isFake());
	}

	@Test
	public void shouldAddAndThenRemoveNewItemFromCache2() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.remove(treeItem);
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA11).isFake());
	}

	@Test
	public void shouldAddAndThenRemoveNewItemFromCache3() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.remove(treeItem);
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA12).isFake());
	}

	@Test
	public void shouldAddAndThenRemoveNewItemFromCache4() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
		scopeTreeWidget.remove(treeItem);
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA1).isFake());
	}

	@Test
	public void shouldAddAndThenRemoveNewItemAndItsChildrenFromCache() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = new ScopeTreeItem(scopeA1);
		scopeTreeWidget.add(treeItem);
		scopeTreeWidget.findScopeTreeItem(scopeA1);
		scopeTreeWidget.findScopeTreeItem(scopeA11);
		scopeTreeWidget.findScopeTreeItem(scopeA12);
		scopeTreeWidget.remove(treeItem);
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA1).isFake());
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA11).isFake());
		assertTrue(scopeTreeWidget.findScopeTreeItem(scopeA12).isFake());
	}
}
