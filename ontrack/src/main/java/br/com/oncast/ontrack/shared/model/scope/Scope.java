package br.com.oncast.ontrack.shared.model.scope;

import java.util.List;

import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparableList;

import com.google.gwt.user.client.rpc.IsSerializable;

// TODO Test this class
public class Scope implements DeeplyComparable, IsSerializable {

	private UUID id;
	private String description;
	private Scope parent;
	private DeeplyComparableList<Scope> childrenList;
	private Release release;
	private Effort effort;

	protected Scope() {}

	public Scope(final String description) {
		this(description, new UUID());
	}

	public Scope(final String description, final UUID scopeId) {
		this.id = scopeId;
		this.description = description;
		this.effort = new Effort();

		childrenList = new DeeplyComparableList<Scope>();
	}

	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public List<Scope> getChildren() {
		return childrenList;
	}

	public Effort getEffort() {
		return effort;
	}

	public Scope add(final Scope scope) {
		childrenList.add(scope);
		scope.parent = this;

		return this;
	}

	public void add(final int beforeIndex, final Scope scope) {
		childrenList.add(beforeIndex, scope);
		scope.parent = this;
	}

	public void remove(final Scope scope) {
		childrenList.remove(scope);
		scope.parent = null;
	}

	public Scope getParent() {
		return parent;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public int getChildIndex(final Scope scope) {
		return childrenList.indexOf(scope);
	}

	public void clearChildren() {
		this.childrenList.clear();
	}

	public void setRelease(final Release release) {
		this.release = release;
	}

	public Release getRelease() {
		return release;
	}

	// TODO Should this method throw an exception if nothing is found or should all 'users' of this method verify for null return?
	public Scope findScope(final UUID scopeId) {
		if (this.id.equals(scopeId)) return this;

		for (final Scope childScope : childrenList) {
			final Scope scopeLoaded = childScope.findScope(scopeId);
			if (scopeLoaded != null) return scopeLoaded;
		}

		return null;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Scope)) return false;

		return this.id.equals(((Scope) obj).getId());
	}

	@Override
	public boolean deepEquals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Scope)) return false;
		final Scope other = (Scope) obj;
		if (childrenList == null) {
			if (other.childrenList != null) return false;
		}
		else if (!childrenList.deepEquals(other.getChildren())) return false;
		if (description == null) {
			if (other.description != null) return false;
		}
		else if (!description.equals(other.description)) return false;
		return true;
	}
}