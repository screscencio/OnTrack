package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.LinkedHashMap;
import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleasePanelWidget extends Composite {

	private static ReleasePanelWidgetUiBinder uiBinder = GWT.create(ReleasePanelWidgetUiBinder.class);

	interface ReleasePanelWidgetUiBinder extends UiBinder<Widget, ReleasePanelWidget> {}

	@UiField
	protected VerticalPanel releasePanel;

	private final LinkedHashMap<Release, ReleaseWidget> childWidgetsMap;

	private Release rootRelease;

	private final ReleaseWidgetFactory releaseWidgetFactory = new ReleaseWidgetFactoryImpl();

	public ReleasePanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		childWidgetsMap = new LinkedHashMap<Release, ReleaseWidget>();
	}

	public void setRelease(final Release rootRelease) {
		this.rootRelease = rootRelease;
		releasePanel.clear();

		for (final Release childRelease : rootRelease.getChildReleases()) {
			createChild(childRelease);
		}
	}

	public void update() {
		final List<Release> releases = rootRelease.getChildReleases();
		for (int i = 0; i < releases.size(); i++) {
			final Release release = releases.get(i);

			final ReleaseWidget releaseWidget = childWidgetsMap.get(release);
			if (releaseWidget == null) {
				createChildAt(release, i);
				continue;
			}

			if (releasePanel.getWidgetIndex(releaseWidget) != i) {
				releasePanel.remove(releaseWidget);
				releasePanel.insert(releaseWidget, i);
			}

			releaseWidget.update();
		}
	}

	private ReleaseWidget createChild(final Release release) {
		return createChildAt(release, childWidgetsMap.size());
	}

	private ReleaseWidget createChildAt(final Release release, final int index) {
		final ReleaseWidget childItem = releaseWidgetFactory.createReleaseWidget(release);
		releasePanel.insert(childItem, index);
		childWidgetsMap.put(release, childItem);
		return childItem;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ReleasePanelWidget)) return false;
		final ReleasePanelWidget other = (ReleasePanelWidget) obj;

		return childWidgetsMap.equals(other.childWidgetsMap);
	}
}
