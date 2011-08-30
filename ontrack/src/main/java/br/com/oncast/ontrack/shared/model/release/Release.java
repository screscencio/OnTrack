package br.com.oncast.ontrack.shared.model.release;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Release implements IsSerializable {

	@IgnoredByDeepEquality
	public static final String SEPARATOR = "/";

	@IgnoredByDeepEquality
	private UUID id;

	private String description;

	@IgnoredByDeepEquality
	private Release parent;

	private List<Release> childrenList;

	private List<Scope> scopeList;

	// IMPORTANT The default constructor is used by GWT to construct new releases. Do not remove this.
	protected Release() {}

	public Release(final String description) {
		this(description, new UUID());
	}

	public Release(final String description, final UUID id) {
		this.description = description;
		this.id = id;

		childrenList = new ArrayList<Release>();
		scopeList = new ArrayList<Scope>();
	}

	public UUID getId() {
		return this.id;
	}

	public String getDescription() {
		return description;
	}

	public String getFullDescription() {
		if (isRoot() || parent.isRoot()) return description;
		return parent.getFullDescription() + SEPARATOR + description;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public Release getParent() {
		return parent;
	}

	/**
	 * Returns the a list with children releases. If you want to add or remove a child release, use {@link Release#addChild(Release)} and
	 * {@link Release#removeChild(Release)}. Don't manipulate this list directly.
	 */
	public List<Release> getChildren() {
		return childrenList;
	}

	public Release getChild(final int index) {
		return childrenList.get(index);
	}

	public int getChildIndex(final Release release) {
		return childrenList.indexOf(release);
	}

	/**
	 * Appends a release to the end of the children list. If the added release already has an association with other release, this association is removed before
	 * the new association is made.
	 */
	public void addChild(final Release release) {
		if (childrenList.contains(release)) return;
		if (release.parent != null) release.parent.removeChild(release);

		childrenList.add(release);
		release.parent = this;
	}

	/**
	 * Inserts a release at the specified position into the children list. If the added release already has an association with other release, this association
	 * is removed before the new association is made.
	 */
	public void addChild(final int beforeIndex, final Release release) {
		childrenList.add(beforeIndex, release);
		release.parent = this;
	}

	/**
	 * Removes a release from the children list. This method does the bidirectional dissociation between a parent release and its child.
	 */
	public void removeChild(final Release release) {
		childrenList.remove(release);
		release.parent = null;
	}

	// TODO +++Test this
	// FIXME Don't create a new release if it doesn't exist. Throw an exception instead.
	public Release loadRelease(final String releaseDescription) {
		final String[] releaseDescriptionSegments = releaseDescription.split(SEPARATOR);

		final String descriptionSegment = releaseDescriptionSegments[0];

		Release childRelease = findDirectChildRelease(descriptionSegment);
		if (childRelease == null) childRelease = createNewSubRelease(descriptionSegment);
		if (releaseDescriptionSegments.length == 1) return childRelease;

		return childRelease.loadRelease(releaseDescription.substring(descriptionSegment.length() + SEPARATOR.length(), releaseDescription.length()));
	}

	public Release findRelease(final UUID releaseId) {
		if (this.id.equals(releaseId)) return this;

		for (final Release release : childrenList) {
			final Release releaseLoaded = release.findRelease(releaseId);
			if (releaseLoaded != null) return releaseLoaded;
		}

		return null;
	}

	/**
	 * Returns the a list with associated scopes. If you want to add or remove a scope from this release, use {@link Release#addScope(Scope)} and
	 * {@link Release#removeScope(Scope)}. Don't manipulate this list directly.
	 */
	public List<Scope> getScopeList() {
		return scopeList;
	}

	/**
	 * Adds a scope to the scope list. If the added scope already has an association with other release, this association is removed before
	 * the new association is made.
	 */
	public void addScope(final Scope scope) {
		if (scopeList.contains(scope)) return;
		if (scope.getRelease() != null) scope.getRelease().removeScope(scope);

		scopeList.add(scope);
		scope.setRelease(this);
	}

	/**
	 * Removes a scope from the scope list. This method does the bidirectional dissociation between release and scope.
	 */
	public void removeScope(final Scope scope) {
		scopeList.remove(scope);
		scope.setRelease(null);
	}

	/**
	 * Removes all scopes from the scope list, doing the bidirectional dissociation between release and each scope in the list.
	 */
	public void removeAllScopes() {
		for (final Scope scope : scopeList) {
			scope.setRelease(null);
		}
		scopeList.clear();
	}

	public float getEffortSum() {
		float effortSum = 0;

		for (final Release childRelease : childrenList)
			effortSum += childRelease.getEffortSum();

		for (final Scope scope : scopeList)
			effortSum += scope.getEffort().getInfered();

		return effortSum;
	}

	public float getAccomplishedEffortSum() {
		float accomplishedEffortSum = 0;

		for (final Release childRelease : childrenList)
			accomplishedEffortSum += childRelease.getAccomplishedEffortSum();

		for (final Scope scope : scopeList)
			accomplishedEffortSum += scope.getEffort().getAccomplishedEffort();

		return accomplishedEffortSum;
	}

	public boolean isDone() {
		if (scopeList.isEmpty() && childrenList.isEmpty()) return false;

		for (final Scope scope : scopeList)
			if (!scope.getProgress().isDone()) return false;

		for (final Release childRelease : childrenList)
			if (!childRelease.isDone()) return false;

		return true;
	}

	private Release createNewSubRelease(final String descriptionSegment) {
		final Release newRelease = new Release(descriptionSegment);
		addChild(newRelease);
		return newRelease;
	}

	private Release findDirectChildRelease(final String releaseDescription) {
		for (final Release release : childrenList)
			if (release.getDescription().toLowerCase().equals(releaseDescription.trim().toLowerCase())) return release;

		return null;
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
}
