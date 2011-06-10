package br.com.oncast.ontrack.shared.util.deeplyComparable;

import java.util.ArrayList;
import java.util.List;

public class DeeplyComparableList<T extends DeeplyComparable> extends ArrayList<T> implements DeeplyComparable {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean deepEquals(final Object element) {
		if (!(element instanceof DeeplyComparableList<?>)) return false;
		final DeeplyComparableList<?> otherList = (DeeplyComparableList<?>) element;
		if (this.size() != otherList.size()) return false;
		final List<T> cloneList = new ArrayList<T>();
		cloneList.addAll(this);
		for (final Object externalElement : otherList) {
			for (final T internalElement : this) {
				if (((DeeplyComparable) externalElement).deepEquals(internalElement)) cloneList.remove(internalElement);
			}
		}
		return cloneList.isEmpty();
	}
}
