package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActionExecutionRequestHandler;
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
	private PlanningActionExecutionRequestHandler planningActionExecutionRequestHandler;

	@Before
	public void setUp() throws Exception {
		final Project project = MockFactory.createProject();
		final ProjectContext projectContext = new ProjectContext(project);

		releasePanel = createReleasePanel();
		releasePanel.setRelease(project.getProjectRelease());

		final Scope scopeBefore = project.getScope();
		scopeUpdated = scopeBefore.getChildren().get(0);

		final List<ActionExecutionListener> listeners = new ArrayList<ActionExecutionListener>();
		listeners.add(releasePanel.getActionExecutionListener());
		planningActionExecutionRequestHandler = new PlanningActionExecutionRequestHandler(projectContext, listeners);
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

		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated, scopeUpdated.getDescription() + " @R1/It1"));

		assertEquals(modifiedReleasePanel, releasePanel);
	}

	@Test
	public void shouldRemoveScopeFromRelease() {
		final Release modifiedRelease = MockFactory.createProject().getProjectRelease();
		modifiedRelease.getChildReleases().get(0).getChildReleases().get(0).addScope(scopeUpdated);

		ReleasePanel modifiedReleasePanel = createReleasePanel();
		modifiedReleasePanel.setRelease(modifiedRelease);

		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated, scopeUpdated.getDescription() + " @R1/It1"));

		assertEquals(modifiedReleasePanel, releasePanel);

		modifiedRelease.getChildReleases().get(0).getChildReleases().get(0).removeScope(scopeUpdated);
		modifiedReleasePanel = createReleasePanel();
		modifiedReleasePanel.setRelease(modifiedRelease);

		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated, scopeUpdated.getDescription()));

		assertEquals(modifiedReleasePanel, releasePanel);
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
