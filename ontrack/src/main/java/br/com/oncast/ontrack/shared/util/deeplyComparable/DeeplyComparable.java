package br.com.oncast.ontrack.shared.util.deeplyComparable;

// +++TODO Remove deepComparable logic, build a "bean-raper" test util framework
public interface DeeplyComparable {
	public boolean deepEquals(Object element);
}
