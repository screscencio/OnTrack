package br.com.oncast.ontrack.shared.model.project;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.effort.FibonacciScale;
import br.com.oncast.ontrack.shared.model.progress.ProgressDefinitionManager;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectContext {

	private final Project project;

	public ProjectContext(final Project project) {
		this.project = project;
		ProgressDefinitionManager.getInstance().populate(project);
	}

	public String getReleaseDescriptionFor(final Release release) {
		if (release == null) return "";
		return release.getFullDescription();
	}

	// TODO +++Cache results???!!!! so that it can be faster (eg. some actions with subactions call this multiple times)
	public Scope findScope(final UUID scopeId) throws ScopeNotFoundException {
		final Scope scope = project.getProjectScope().findScope(scopeId);
		if (scope == null) throw new ScopeNotFoundException("The scope referenced by id " + scopeId + " was not found.");
		return scope;
	}

	// TODO +++Cache results???!!!! so that it can be faster (eg. some actions with subactions call this multiple times)
	public Release findRelease(final UUID releaseId) throws ReleaseNotFoundException {
		final Release release = project.getProjectRelease().findRelease(releaseId);
		if (release == null) throw new ReleaseNotFoundException("The release referenced by id " + releaseId + " was not found.");
		return release;
	}

	// TODO +++Cache results???!!!! so that it can be faster (eg. some actions with subactions call this multiple times)
	public Release findRelease(final String releaseDescription) throws ReleaseNotFoundException {
		if (releaseDescription == null || releaseDescription.isEmpty()) return null;

		final String releaseLoadQuery = removeProjectReleaseDescription(releaseDescription);

		return project.getProjectRelease().findRelease(releaseLoadQuery);
	}

	public Scope getProjectScope() {
		return project.getProjectScope();
	}

	public Release getProjectRelease() {
		return project.getProjectRelease();
	}

	public List<Release> getDescendantReleases() {
		return project.getProjectRelease().getDescendantReleases();
	}

	public Set<String> getProgressDefinitions() {
		return ProgressDefinitionManager.getInstance().getProgressDefinitions();
	}

	public List<String> getFibonacciScaleForEffort() {
		return FibonacciScale.getFibonacciScaleList();
	}

	private String removeProjectReleaseDescription(final String releaseDescription) {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(releaseDescription);
		final String releaseLoadQuery = !parser.getHeadRelease().equals(project.getProjectRelease().getDescription()) ? releaseDescription
				: parser
						.getTailReleases();
		return releaseLoadQuery;
	}
}
