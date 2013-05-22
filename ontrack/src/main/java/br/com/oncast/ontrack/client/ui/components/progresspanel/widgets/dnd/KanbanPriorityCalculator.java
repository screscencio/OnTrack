package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.scope.ScopeCardWidget;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.user.client.ui.VerticalPanel;

public class KanbanPriorityCalculator {
	private final Release release;
	private final VerticalPanel dropTarget;

	public KanbanPriorityCalculator(final Release release, final VerticalPanel dropTarget) {
		this.release = release;
		this.dropTarget = dropTarget;
	}

	public int getNewPriority(final ScopeCardWidget scopeWidget) {
		final int widgetIndex = dropTarget.getWidgetIndex(scopeWidget);

		final int currentPriority = getPriorityOfWidget(scopeWidget);

		final int nextPriority = getPriorityOfWidgetAtIndex(widgetIndex + 1);
		if (isIncreasedPriority(currentPriority, nextPriority)) return nextPriority;

		final int prevPriority = getPriorityOfWidgetAtIndex(widgetIndex - 1);
		if (isDecreasedPriority(currentPriority, prevPriority)) return prevPriority;

		return currentPriority;
	}

	private boolean isDecreasedPriority(final int currentPriority, final int prevPriority) {
		return prevPriority < Integer.MAX_VALUE && currentPriority < prevPriority;
	}

	private boolean isIncreasedPriority(final int currentPriority, final int nextPriority) {
		return nextPriority >= 0 && currentPriority > nextPriority;
	}

	private int getPriorityOfWidgetAtIndex(final int index) {
		if (index < 0) return Integer.MAX_VALUE;
		if (index >= dropTarget.getWidgetCount()) return Integer.MIN_VALUE;
		return getPriorityOfWidget((ScopeCardWidget) dropTarget.getWidget(index));
	}

	private int getPriorityOfWidget(final ScopeCardWidget scopeWidget) {
		return release.getScopeIndex(scopeWidget.getModelObject());
	}
}