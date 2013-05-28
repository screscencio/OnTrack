package br.com.oncast.ontrack.client.ui.component.scopetree;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.ClientServicesTestUtils;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ReleaseTag;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.Effort;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.mocks.EffortDeepEqualityComparator;
import br.com.oncast.ontrack.utils.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

@GwtModule("br.com.oncast.ontrack.Application")
public class BindReleaseTest extends GwtTest {

	private Scope firstScope;
	private ScopeTree tree;
	private ProjectContext projectContext;
	private ActionExecutionServiceImpl actionExecutionService;

	@BeforeClass
	public static void beforeClass() throws Exception {
		DeepEqualityTestUtils.setCustomDeepEqualityComparator(Effort.class, new EffortDeepEqualityComparator());
		ClientServicesTestUtils.configure().mockEssential();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		DeepEqualityTestUtils.removeCustomDeepEqualityComparator(Effort.class);
		ClientServicesTestUtils.reset();
	}

	@Before
	public void setUp() throws Exception {
		projectContext = ProjectTestUtils.createProjectContext(getScope(), ReleaseTestUtils.getRelease());
		tree = new ScopeTree();
		tree.setContext(projectContext);

		actionExecutionService = ActionExecutionFactoryTestUtil.create(projectContext);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() {
		final Scope rootScope = ScopeTestUtils.createScope("Project");
		firstScope = ScopeTestUtils.createScope("1");
		rootScope.add(firstScope);
		rootScope.add(ScopeTestUtils.createScope("2"));

		return rootScope;
	}

	@Test
	public void shouldInsertTagIntoScopeTreeItem() throws Exception {
		actionExecutionService.onUserActionExecutionRequest(new ScopeBindReleaseAction(firstScope.getId(), "R1"));

		final ReleaseTag tag = getTag();

		assertTrue(tag.isVisible());
		assertEquals("R1", tag.getText());
	}

	@Test
	public void shouldRemoveTagFromScopeTreeItemWhenUnbindingScopeFromRelease() throws Exception {
		final Release release = projectContext.getProjectRelease().getChild(0);
		release.addScope(firstScope);
		assertEquals(release, firstScope.getRelease());

		actionExecutionService.onUserActionExecutionRequest(new ScopeBindReleaseAction(firstScope.getId(), ""));

		final ReleaseTag tag = getTag();

		assertFalse(tag.isVisible());
		assertTrue(tag.getText().isEmpty());
	}

	private ReleaseTag getTag() throws ScopeNotFoundException, NoSuchFieldException, IllegalAccessException {
		final ScopeTreeWidget asWidget = (ScopeTreeWidget) tree.asWidget();
		final ScopeTreeItem item = asWidget.findScopeTreeItem(firstScope);

		final Field scopeItemWidgetField = item.getClass().getDeclaredField("scopeItemWidget");
		scopeItemWidgetField.setAccessible(true);
		final ScopeTreeItemWidget itemWidget = (ScopeTreeItemWidget) scopeItemWidgetField.get(item);

		final Field releaseTagField = itemWidget.getClass().getDeclaredField("releaseTag");
		releaseTagField.setAccessible(true);
		final ReleaseTag tag = (ReleaseTag) releaseTagField.get(itemWidget);
		return tag;
	}
}
