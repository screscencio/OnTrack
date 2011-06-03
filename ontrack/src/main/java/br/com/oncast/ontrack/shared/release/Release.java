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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childrenList == null) ? 0 : childrenList.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((scopeList == null) ? 0 : scopeList.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Release)) return false;
		final Release other = (Release) obj;
		if (childrenList == null) {
			if (other.childrenList != null) return false;
		}
		else if (!childrenList.equals(other.childrenList)) return false;
		if (description == null) {
			if (other.description != null) return false;
		}
		else if (!description.equals(other.description)) return false;
		if (scopeList == null) {
			if (other.scopeList != null) return false;
		}
		else if (!scopeList.equals(other.scopeList)) return false;
		return true;
	}

}
