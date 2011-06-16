package br.com.oncast.ontrack.shared.release;

import java.util.List;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparableList;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Release implements DeeplyComparable, IsSerializable {

	public static final String SEPARATOR = "/";
	private UUID id;

	private String description;
	private Release parent;
	private DeeplyComparableList<Release> childrenList;
	private DeeplyComparableList<Scope> scopeList;

	protected Release() {}

	public Release(final String description) {
		this.id = new UUID();
		this.description = description;

		childrenList = new DeeplyComparableList<Release>();
		scopeList = new DeeplyComparableList<Scope>();
	}

	public UUID getId() {
		return this.id;
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
		return this.id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Release)) return false;
		return this.id.equals(((Release) obj).getId());
	}

	@Override
	public boolean deepEquals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Release)) return false;
		final Release other = (Release) obj;
		if (childrenList == null) {
			if (other.childrenList != null) return false;
		}
		else if (!childrenList.deepEquals(other.childrenList)) return false;
		if (description == null) {
			if (other.description != null) return false;
		}
		else if (!description.equals(other.description)) return false;
		if (scopeList == null) {
			if (other.scopeList != null) return false;
		}
		else if (!scopeList.deepEquals(other.scopeList)) return false;
		return true;
	}

	public Release findRelease(final UUID releaseId) {
		if (this.id.equals(releaseId)) return this;

		for (final Release release : childrenList) {
			final Release releaseLoaded = release.findRelease(releaseId);
			if (releaseLoaded != null) return releaseLoaded;
		}

		return null;
	}

}
