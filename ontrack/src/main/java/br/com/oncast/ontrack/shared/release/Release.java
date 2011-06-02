package br.com.oncast.ontrack.shared.release;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.scope.Scope;

public class Release {

	public static final String SEPARATOR = "/";

	private final String description;
	private Release parent;
	private final List<Release> childrenList;
	private final List<Scope> scopeList;

	public Release(final String description) {
		this.description = description;

		childrenList = new ArrayList<Release>();
		scopeList = new ArrayList<Scope>();
	}

	public String getDescription() {
		return description;
	}

	public String getFullDescription() {
		if (isRoot()) return description;
		return parent.getFullDescription() + SEPARATOR + description;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public void addRelease(final Release release) {
		if (childrenList.contains(release)) return;
		childrenList.add(release);
		release.parent = this;
	}

	// TODO Test this
	public Release loadRelease(final String releaseDescription) {
		final String[] releaseDescriptionSegments = releaseDescription.split(SEPARATOR);

		final String descriptionSegment = releaseDescriptionSegments[0];

		Release childRelease = findDirectChildRelease(descriptionSegment);
		if (childRelease == null) childRelease = createNewSubRelease(descriptionSegment);
		if (releaseDescriptionSegments.length == 1) return childRelease;

		return childRelease.loadRelease(releaseDescription.substring(descriptionSegment.length() + SEPARATOR.length(), releaseDescription.length()));
	}

	private Release createNewSubRelease(final String descriptionSegment) {
		final Release newRelease = new Release(descriptionSegment);
		addRelease(newRelease);
		return newRelease;
	}

	private Release findDirectChildRelease(final String releaseDescription) {
		for (final Release release : childrenList)
			if (release.getDescription().toLowerCase().equals(releaseDescription.trim().toLowerCase())) return release;

		return null;
	}

	public List<Release> getChildReleases() {
		return childrenList;
	}

	public List<Scope> getScopeList() {
		return scopeList;
	}

	public void addScope(final Scope scope) {
		if (scopeList.contains(scope)) return;
		scopeList.add(scope);
	}

	public void removeScope(final Scope selectedScope) {
		scopeList.remove(selectedScope);
	}

	// TODO Review this when it have a persistence strategy. Should it use id instead?
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Release)) return false;
		final Release other = (Release) obj;

		if (description == null) {
			if (other.getDescription() != null) return false;
		}
		else if (!description.equals(other.getDescription())) return false;

		if (childrenList.size() != other.getChildReleases().size()) return false;
		if (!childrenList.equals(other.getChildReleases())) return false;

		return true;
	}
}
