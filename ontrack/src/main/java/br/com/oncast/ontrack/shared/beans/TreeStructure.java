package br.com.oncast.ontrack.shared.beans;

import java.util.List;

public interface TreeStructure<T> {

	public abstract List<T> getChildren();

	public abstract void add(final T treeStructure);

	public abstract void add(final int beforeIndex, final T treeStructure);

	public abstract void remove(final T treeStructure);

	public abstract TreeStructure<T> getParent();

	public abstract int getIndex();

	public abstract boolean isRoot();

}