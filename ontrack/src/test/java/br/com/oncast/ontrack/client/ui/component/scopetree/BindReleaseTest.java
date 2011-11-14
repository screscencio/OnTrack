package br.com.oncast.ontrack.client.ui.component.scopetree;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.Tag;
import br.com.oncast.ontrack.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.shared.model.actions.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.mocks.EffortDeepEqualityComparator;

import com.octo.gwt.test.GwtTest;

public class BindReleaseTest extends GwtTest {

	private Scope firstScope;
	private ScopeTree tree;
	private ProjectContext projectContext;
	private ActionExecutionServiceImpl actionExecutionService;

	@BeforeClass
	public static void beforeClass() {
		DeepEqualityTestUtils.setCustomDeepEqualityComparator(Effort.class, new EffortDeepEqualityComparator());
	}

	@AfterClass
	public static void afterClass() {
		DeepEqualityTestUtils.removeCustomDeepEqualityComparator(Effort.class);
	}

	@Before
	public void setUp() {
		projectContext = new ProjectContext(new Project(getScope(), ReleaseTestUtils.getRelease()));
		tree = new ScopeTree();
		tree.setContext(projectContext);

		actionExecutionService = ActionExecutionFactoryTestUtil.create(projectContext);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() {
		final Scope rootScope = new Scope("Project");
		firstScope = new Scope("1");
		rootScope.add(firstScope);
		rootScope.add(new Scope("2"));

		return rootScope;
	}

	@Test
	public void shouldInsertTagIntoScopeTreeItem() throws Exception {
		actionExecutionService.onUserActionExecutionRequest(new ScopeBindReleaseAction(firstScope.getId(), "R1"));

		final Tag tag = getTag();

		assertTrue(tag.isVisible());
		assertEquals("R1", tag.getText());
	}

	@Test
	public void shouldRemoveTagFromScopeTreeItemWhenUnbindingScopeFromRelease() throws Exception {
		final Release release = projectContext.getProjectRelease().getChild(0);
		release.addScope(firstScope);
		assertEquals(release, firstScope.getRelease());

		actionExecutionService.onUserActionExecutionRequest(new ScopeBindReleaseAction(firstScope.getId(), ""));

		final Tag tag = getTag();

		assertFalse(tag.isVisible());
		assertTrue(tag.getText().isEmpty());
	}

	private Tag getTag() throws ScopeNotFoundException, NoSuchFieldException, IllegalAccessException {
		final ScopeTreeWidget asWidget = (ScopeTreeWidget) tree.asWidget();
		final ScopeTreeItem item = asWidget.findScopeTreeItem(firstScope.getId());

		final Field scopeItemWidgetField = item.getClass().getDeclaredField("scopeItemWidget");
		scopeItemWidgetField.setAccessible(true);
		final ScopeTreeItemWidget itemWidget = (ScopeTreeItemWidget) scopeItemWidgetField.get(item);

		final Field releaseTagField = itemWidget.getClass().getDeclaredField("releaseTag");
		releaseTagField.setAccessible(true);
		final Tag tag = (Tag) releaseTagField.get(itemWidget);
		return tag;
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
