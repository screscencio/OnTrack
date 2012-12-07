package br.com.oncast.ontrack.client.ui.components.organization.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseEffortBasedHorizontalPanel extends HorizontalPanel {

	private final Release release;

	public ReleaseEffortBasedHorizontalPanel(final Release release) {
		this.release = release;
	}

	@Override
	public void add(final Widget w) {
		super.add(w);
		setCellWidth(w, getRelativeSize((ReleaseSummaryWidget) w));
	}

	@Override
	public void insert(final Widget w, final int beforeIndex) {
		super.insert(w, beforeIndex);
		setCellWidth(w, getRelativeSize((ReleaseSummaryWidget) w));
	}

	private String getRelativeSize(final ReleaseSummaryWidget w) {
		final Release r = w.getModelObject();
		final float releaseEffort = r.getEffortSum();
		final float totalEffort = release.getEffortSum();
		w.adjustMinWidth(releaseEffort / totalEffort);
		return releaseEffort == 0 ? "1" : (releaseEffort / totalEffort * 100) + "%";
	}

}
