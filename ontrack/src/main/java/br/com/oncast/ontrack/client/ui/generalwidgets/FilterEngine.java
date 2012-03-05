package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HasText;

public class FilterEngine<T extends HasText> {

	private final List<T> itens;
	private final FilterResultListener<T> listener;

	public FilterEngine(final List<T> itens, final FilterResultListener<T> listener) {
		this.itens = itens;
		this.listener = listener;
	}

	public void filterMenuItens(final String text) {
		final String filterText = text.trim();
		listener.onFilterActivationChanged(!filterText.isEmpty());

		final List<T> filteredItens = getFilteredItens(filterText);

		final boolean shouldAddCustomIten = !filterText.isEmpty() && !hasTextMatchInItemList(filteredItens, filterText);

		listener.onUpdateItens(filteredItens, shouldAddCustomIten, filterText);
	}

	private boolean hasTextMatchInItemList(final List<T> filteredItens, final String text) {
		for (final HasText item : filteredItens)
			if (item.getText().toLowerCase().equals(text.toLowerCase())) return true;
		return false;
	}

	private List<T> getFilteredItens(final String filterText) {
		if (filterText.isEmpty()) return new ArrayList<T>(itens);

		final String lowerCaseFilterText = filterText.toLowerCase();

		int itensStartingWithIndex = 0;
		final List<T> filteredItens = new ArrayList<T>();
		for (final T item : itens) {
			final String itemText = item.getText().toLowerCase();
			if (itemText.contains(lowerCaseFilterText)) {
				if (itemText.startsWith(lowerCaseFilterText)) filteredItens.add(itensStartingWithIndex++, item);
				else filteredItens.add(item);
			}
		}
		return filteredItens;
	}

	public interface FilterResultListener<T extends HasText> {

		void onFilterActivationChanged(boolean isActive);

		void onUpdateItens(List<T> filteredItens, boolean shouldAddCustomItem, String filterText);
	}

}
