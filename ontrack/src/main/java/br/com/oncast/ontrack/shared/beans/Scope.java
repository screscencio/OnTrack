package br.com.oncast.ontrack.shared.beans;

import java.util.ArrayList;
import java.util.List;

public class Scope {

	private final List<Scope> childrenList;
	private String description;
	private Scope parent;

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

	public void add(final Scope scope) {
		childrenList.add(scope);
		scope.parent = this;
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

	public int getIndex() {
		if (isRoot()) return 0;
		return parent.childrenList.indexOf(this);
	}

	public boolean isRoot() {
		return parent == null;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Scope)) return false;
		final Scope otherScope = (Scope) other;

		if (!this.getDescription().equals(otherScope.getDescription())) return false;

		for (int i = 0; i < this.getChildren().size(); i++) {
			if (!this.getChildren().get(i).equals(otherScope.getChildren().get(i))) return false;
		}
		return true;
	}
}