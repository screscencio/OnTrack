package br.com.oncast.ontrack.client.ui.components.releasepanel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

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

	private Scope scope;
	private Scope firstScope;
	private ReleasePanel releasePanel;
	private ProjectContext projectContext;
	private PlanningActionExecutionRequestHandler planningActionExecutionRequestHandler;

	@Before
	public void setUp() {
		final Project project = MockFactory.createProject();
		projectContext = new ProjectContext(project);

		releasePanel = new ReleasePanel();
		releasePanel.setRelease(project.getProjectRelease());

		scope = project.getScope();
		firstScope = scope.getChildren().get(0);

		final List<ActionExecutionListener> listeners = new ArrayList<ActionExecutionListener>();
		listeners.add(releasePanel.getActionExecutionListener());
		planningActionExecutionRequestHandler = new PlanningActionExecutionRequestHandler(projectContext, listeners);
	}

	// TODO Testar os equals sem a comparação de tamanho
	@Test
	public void shouldUpdateReleaseWithNewScope() {
		final ReleasePanel modifiedReleasePanel = new ReleasePanel();
		modifiedReleasePanel.setRelease(getModifiedRelease());
		System.out.println("");
		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeUpdateAction(firstScope, firstScope.getDescription() + " @R1/It1"));

		assertEquals(modifiedReleasePanel, releasePanel);
	}

	private Release getModifiedRelease() {
		final Release projectRelease = new Release("project");
		final Release r1 = new Release("R1");
		final Release r2 = new Release("R2");
		final Release r3 = new Release("R3");
		final Release it1 = new Release("It1");
		final Release it2 = new Release("It2");
		final Release it3 = new Release("It3");
		final Release it4 = new Release("It4");
		it4.addScope(firstScope);

		projectRelease.addRelease(r1);
		projectRelease.addRelease(r2);
		projectRelease.addRelease(r3);
		r1.addRelease(it1);
		r1.addRelease(it2);
		r1.addRelease(it3);
		r2.addRelease(it4);

		return projectRelease;
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
