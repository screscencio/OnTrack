package br.com.oncast.ontrack.shared.beans;

import java.util.ArrayList;
import java.util.List;

public class Scope implements TreeStructure<Scope> {

	private final List<Scope> childrenList;
	private final String description;
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

	/**
	 * @see br.com.oncast.ontrack.shared.beans.TreeStructure#getChildren()
	 */
	@Override
	public List<Scope> getChildren() {
		return childrenList;
	}

	/**
	 * @see br.com.oncast.ontrack.shared.beans.TreeStructure#add(br.com.oncast.ontrack.shared.beans.Scope)
	 */
	@Override
	public void add(final Scope scope) {
		childrenList.add(scope);
		scope.parent = this;
	}

	/**
	 * @see br.com.oncast.ontrack.shared.beans.TreeStructure#add(int, br.com.oncast.ontrack.shared.beans.Scope)
	 */
	@Override
	public void add(final int beforeIndex, final Scope scope) {
		childrenList.add(beforeIndex, scope);
		scope.parent = this;
	}

	/**
	 * @see br.com.oncast.ontrack.shared.beans.TreeStructure#remove(br.com.oncast.ontrack.shared.beans.Scope)
	 */
	@Override
	public void remove(final Scope scope) {
		childrenList.remove(scope);
		scope.parent = null;
	}

	/**
	 * @see br.com.oncast.ontrack.shared.beans.TreeStructure#getParent()
	 */
	@Override
	public Scope getParent() {
		return parent;
	}

	/**
	 * @see br.com.oncast.ontrack.shared.beans.TreeStructure#getIndex()
	 */
	@Override
	public int getIndex() {
		if (isRoot()) return 0;
		return parent.childrenList.indexOf(this);
	}

	/**
	 * @see br.com.oncast.ontrack.shared.beans.TreeStructure#isRoot()
	 */
	@Override
	public boolean isRoot() {
		return parent == null;
	}
}
