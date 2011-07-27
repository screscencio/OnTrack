package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.mocks.ContextProviderServiceMock;
import br.com.oncast.ontrack.mocks.MockFactory;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbolsProvider;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

import com.octo.gwt.test.GwtTest;

public class UpdateTest extends GwtTest {

	private Scope scopeUpdated;
	private ReleasePanel releasePanel;
	private ActionExecutionService actionExecutionService;

	@Before
	public void setUp() throws Exception {
		final Project project = MockFactory.createProject();
		final ProjectContext projectContext = new ProjectContext(project);

		releasePanel = new ReleasePanel();
		releasePanel.setRelease(project.getProjectRelease());

		final Scope scopeBefore = project.getProjectScope();
		scopeUpdated = scopeBefore.getChildren().get(0);

		final ContextProviderService contextService = new ContextProviderServiceMock(projectContext);
		actionExecutionService = new ActionExecutionService(contextService);
		actionExecutionService.addActionExecutionListener(releasePanel.getActionExecutionListener());
	}

	@Test
	public void shouldUpdateReleaseWithNewScope() {
		final Release modifiedRelease = MockFactory.createProject().getProjectRelease();
		modifiedRelease.getChildReleases().get(0).getChildReleases().get(0).addScope(scopeUpdated);

		final ReleasePanel modifiedReleasePanel = new ReleasePanel();
		modifiedReleasePanel.setRelease(modifiedRelease);

		actionExecutionService.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated.getId(), scopeUpdated.getDescription() + " "
				+ StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "R1/It1"));

		DeepEqualityTestUtils.assertObjectEquality(releasePanel, modifiedReleasePanel);
	}

	@Test
	public void shouldRemoveScopeFromRelease() {
		final Release modifiedRelease = MockFactory.createProject().getProjectRelease();
		modifiedRelease.getChildReleases().get(0).getChildReleases().get(0).addScope(scopeUpdated);

		ReleasePanel modifiedReleasePanel = new ReleasePanel();
		modifiedReleasePanel.setRelease(modifiedRelease);

		actionExecutionService.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated.getId(), scopeUpdated.getDescription() + " "
				+ StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "R1/It1"));

		DeepEqualityTestUtils.assertObjectEquality(releasePanel, modifiedReleasePanel);

		modifiedRelease.getChildReleases().get(0).getChildReleases().get(0).removeScope(scopeUpdated);
		modifiedReleasePanel = new ReleasePanel();
		modifiedReleasePanel.setRelease(modifiedRelease);

		actionExecutionService.onActionExecutionRequest(new ScopeUpdateAction(scopeUpdated.getId(), scopeUpdated.getDescription()));

		DeepEqualityTestUtils.assertObjectEquality(releasePanel, modifiedReleasePanel);
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
