package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actions.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.mocks.MockFactory;
import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;

import com.octo.gwt.test.GwtTest;

public class UpdateTest extends GwtTest {

	private Scope scopeUpdated;
	private ReleasePanel releasePanel;
	private ActionExecutionService actionExecutionService;

	@Before
	public void setUp() throws Exception {
		final Project project = MockFactory.createProject();
		final ProjectContext projectContext = new ProjectContext(project);

		releasePanel = createReleasePanel();
		releasePanel.setRelease(project.getProjectRelease());

		final Scope scopeBefore = project.getProjectScope();
		scopeUpdated = scopeBefore.getChildren().get(0);

		final ContextProviderService contextService = new ContextProviderService();
		contextService.setProjectContext(projectContext);
		actionExecutionService = new ActionExecutionService(contextService);
		actionExecutionService.addActionExecutionListener(releasePanel.getActionExecutionListener());
	}

	private ReleasePanel createReleasePanel() {
		final ReleasePanel releasePanelMock = new ReleasePanel();

		Field widgetField;
		try {
			widgetField = ReleasePanel.class.getDeclaredField("releasePanelWidget");
			widgetField.setAccessible(true);
			final ReleasePanelWidget releasePanelWidget = (ReleasePanelWidget) widgetField.get(releasePanelMock);

			final Field factoryField = ReleasePanelWidget.class.getDeclaredField("releaseWidgetFactory");
			factoryField.setAccessible(true);
			factoryField.set(releasePanelWidget, new ReleaseWidgetFactoryMock());
		}
		catch (final Exception e) {
			e.printStackTrace();
			fail("ReleasePanel could not be created.");
		}

		return releasePanelMock;
	}

	@Test
	public void shouldUpdateReleaseWithNewScope() {
		final Release modifiedRelease = MockFactory.createProject().getProjectRelease();
		modifiedRelease.getChildReleases().get(0).getChildReleases().get(0).addScope(scopeUpdated);

		final ReleasePanel modifiedReleasePanel = createReleasePanel();
		modifiedReleasePanel.setRelease(modifiedRelease);

		actionExecutionService.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated, scopeUpdated.getDescription() + " @R1/It1"));

		assertTrue(modifiedReleasePanel.deepEquals(releasePanel));
	}

	@Test
	public void shouldRemoveScopeFromRelease() {
		final Release modifiedRelease = MockFactory.createProject().getProjectRelease();
		modifiedRelease.getChildReleases().get(0).getChildReleases().get(0).addScope(scopeUpdated);

		ReleasePanel modifiedReleasePanel = createReleasePanel();
		modifiedReleasePanel.setRelease(modifiedRelease);

		actionExecutionService.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated, scopeUpdated.getDescription() + " @R1/It1"));

		assertTrue(modifiedReleasePanel.deepEquals(releasePanel));

		modifiedRelease.getChildReleases().get(0).getChildReleases().get(0).removeScope(scopeUpdated);
		modifiedReleasePanel = createReleasePanel();
		modifiedReleasePanel.setRelease(modifiedRelease);

		actionExecutionService.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated, scopeUpdated.getDescription()));

		assertTrue(modifiedReleasePanel.deepEquals(releasePanel));
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
