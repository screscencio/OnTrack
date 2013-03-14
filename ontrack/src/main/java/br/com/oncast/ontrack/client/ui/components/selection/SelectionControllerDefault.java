package br.com.oncast.ontrack.client.ui.components.selection;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;

public class SelectionControllerDefault implements SelectionController {

	private final List<IsSelectable> selectables = new ArrayList<IsSelectable>();
	Integer currentSelectionIndex = null;

	public void addSelectableWidget(final IsSelectable selected) {
		selectables.add(selected);
	}

	@Override
	public void selectNext() {
		new Timer() {

			@Override
			public void run() {
				verifyAndUpdateCurrentSelection();
				if (currentSelectionIndex == null) return;
				final int referenceIndex = currentSelectionIndex;
				final int destinationIndex = referenceIndex >= selectables.size() - 1 ? 0 : referenceIndex + 1;
				selectables.get(referenceIndex).deselect();
				selectables.get(destinationIndex).select();
			}
		}.schedule(100);
	}

	@Override
	public void selectPrevious() {
		new Timer() {

			@Override
			public void run() {
				verifyAndUpdateCurrentSelection();
				if (currentSelectionIndex == null) return;
				final int referenceIndex = currentSelectionIndex;
				final int destinationIndex = referenceIndex <= 0 ? selectables.size() - 1 : referenceIndex - 1;
				selectables.get(referenceIndex).deselect();
				selectables.get(destinationIndex).select();
			}
		}.schedule(100);
	}

	@Override
	public void setSelected(final IsSelectable selectable) {
		currentSelectionIndex = selectables.indexOf(selectable);
		verifyAndUpdateCurrentSelection();
	}

	private void verifyAndUpdateCurrentSelection() {
		if (selectables.size() == 0) {
			currentSelectionIndex = null;
			return;
		}

		if (!(currentSelectionIndex != null && selectables.get(currentSelectionIndex).isSelected())) {
			for (int i = 0; i < selectables.size(); i++) {
				final IsSelectable selectable = selectables.get(i);
				if (!selectable.isSelected()) continue;
				currentSelectionIndex = i;
			}
		}

		deselectOthers();
	}

	private void deselectOthers() {
		for (int i = 0; i < selectables.size(); i++) {
			if (currentSelectionIndex != null && i == currentSelectionIndex) continue;
			selectables.get(i).deselect();
		}
	}

	@Override
	public void deselectAll() {
		currentSelectionIndex = null;
		deselectOthers();
	}
}
