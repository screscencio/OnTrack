package br.com.oncast.ontrack.shared.scope;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;

public class Scope {

	private final List<Scope> childrenList;
	private String description;
	private Scope parent;
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

	// Refactor this so that it uses this object id
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Scope)) return false;
		final Scope otherScope = (Scope) other;

		if (!this.getDescription().equals(otherScope.getDescription())) return false;
		if (this.getChildren().size() != otherScope.getChildren().size()) return false;

		for (int i = 0; i < this.getChildren().size(); i++) {
			if (!this.getChildren().get(i).equals(otherScope.getChildren().get(i))) return false;
		}
		return true;
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
}