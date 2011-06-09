package br.com.oncast.ontrack.shared.release;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

public class Release {

	public static final String SEPARATOR = "/";
	private final UUID uuid;

	private final String description;
	private Release parent;
	private final List<Release> childrenList;
	private final List<Scope> scopeList;

	public Release(final String description) {
		this(description, null);
	}

	public Release(final String description, final String uuid) {
		this.description = description;

		childrenList = new ArrayList<Release>();
		scopeList = new ArrayList<Scope>();
		if (uuid != null) this.uuid = new UUID(uuid);
		else this.uuid = new UUID();
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
		return this.uuid.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Release)) return false;

		return this.uuid.equals(((Release) obj).getUuid());
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public boolean deepEquals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Release)) return false;
		final Release other = (Release) obj;
		if (childrenList == null) {
			if (other.childrenList != null) return false;
		}
		else if (!compareChildrenLists(other.childrenList)) return false;
		if (description == null) {
			if (other.description != null) return false;
		}
		else if (!description.equals(other.description)) return false;
		if (scopeList == null) {
			if (other.scopeList != null) return false;
		}
		else if (!compareScopeLists(other.scopeList)) return false;
		return true;
	}

	private boolean compareChildrenLists(final List<Release> otherList) {
		if (childrenList.size() != otherList.size()) return false;
		final List<Release> cloneList = new ArrayList<Release>();
		cloneList.addAll(childrenList);
		for (final Release release : otherList) {
			for (final Release childScope : childrenList) {
				if (release.deepEquals(childScope)) cloneList.remove(childScope);
			}
		}

		return cloneList.isEmpty();
	}

	private boolean compareScopeLists(final List<Scope> otherList) {
		if (scopeList.size() != otherList.size()) return false;
		final List<Scope> cloneList = new ArrayList<Scope>();
		cloneList.addAll(scopeList);
		for (final Scope scope : otherList) {
			for (final Scope childScope : scopeList) {
				if (scope.deepEquals(childScope)) cloneList.remove(childScope);
			}
		}

		return cloneList.isEmpty();
	}

}
