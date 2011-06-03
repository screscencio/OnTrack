package br.com.oncast.ontrack.shared.scope;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;

// TODO Test this class
public class Scope {

	private String description;
	private Scope parent;
	private final List<Scope> childrenList;
	private Release release;

	public Scope(final String description) {
		this(description, null);
	}

	public Scope(final String description, final Scope parent) {
		this.description = description;
		this.parent = parent;
		childrenList = new ArrayList<Scope>();
	}

	public String getDescription() {
		return description;
	}

	public List<Scope> getChildren() {
		return childrenList;
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

	// TODO Review this when it have a persistence strategy. Should it use id instead?
	// @Override
	// public boolean equals(final Object obj) {
	// if (this == obj) return true;
	// if (!(obj instanceof Scope)) return false;
	// final Scope other = (Scope) obj;
	//
	// if (description == null) {
	// if (other.getDescription() != null) return false;
	// }
	// else if (!description.equals(other.getDescription())) return false;
	//
	// if (this.getChildren().size() != other.getChildren().size()) return false;
	// for (int i = 0; i < this.getChildren().size(); i++) {
	// if (!this.getChildren().get(i).equals(other.getChildren().get(i))) return false;
	// }
	// return true;
	// }
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childrenList == null) ? 0 : childrenList.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Scope)) return false;
		final Scope other = (Scope) obj;
		if (childrenList == null) {
			if (other.childrenList != null) return false;
		}
		else if (!childrenList.equals(other.childrenList)) return false;
		if (description == null) {
			if (other.description != null) return false;
		}
		else if (!description.equals(other.description)) return false;
		return true;
	}

}