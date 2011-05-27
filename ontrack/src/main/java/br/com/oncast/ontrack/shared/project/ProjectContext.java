package br.com.oncast.ontrack.shared.project;

import br.com.oncast.ontrack.shared.release.Release;

public class ProjectContext {

	private final Project project;

	public ProjectContext(final Project project) {
		this.project = project;
	}

	public String getReleaseDescription(final Release release) {
		if (release == null) return "";
		return release.getFullDescription();
	}

	public Release loadRelease(final String releaseDescription) {
		if (releaseDescription == null || releaseDescription.isEmpty()) return null;
		return project.getProjectRelease().loadRelease(releaseDescription);
	}
}
