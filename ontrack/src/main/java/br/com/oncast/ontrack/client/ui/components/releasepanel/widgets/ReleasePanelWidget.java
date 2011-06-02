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
	protected VerticalPanel releaseContainer;

	private final LinkedHashMap<Release, ReleaseWidget> releaseWidgetsMap;

	private Release release;

	private final ReleaseWidgetFactory releaseWidgetFactory = new ReleaseWidgetFactoryImpl();

	public ReleasePanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		releaseWidgetsMap = new LinkedHashMap<Release, ReleaseWidget>();
	}

	public void setRelease(final Release rootRelease) {
		this.release = rootRelease;
		releaseContainer.clear();

		for (final Release childRelease : rootRelease.getChildReleases()) {
			createChildReleaseWidget(childRelease);
		}
	}

	// TODO Extract widget that encapsulates all this logic
	public void update() {
		final List<Release> releaseList = release.getChildReleases();
		for (int i = 0; i < releaseList.size(); i++) {
			final Release release = releaseList.get(i);

			final ReleaseWidget releaseWidget = releaseWidgetsMap.get(release);
			if (releaseWidget == null) {
				createChildReleaseWidgetAt(release, i);
				continue;
			}

			if (releaseContainer.getWidgetIndex(releaseWidget) != i) {
				releaseContainer.remove(releaseWidget);
				releaseContainer.insert(releaseWidget, i);
			}

			releaseWidget.update();
		}
		for (int i = releaseList.size(); i < releaseContainer.getWidgetCount(); i++) {
			final ReleaseWidget releaseWidget = (ReleaseWidget) releaseContainer.getWidget(i);
			releaseContainer.remove(i);
			releaseWidgetsMap.remove(releaseWidget.getRelease());
		}
	}

	private ReleaseWidget createChildReleaseWidget(final Release release) {
		return createChildReleaseWidgetAt(release, releaseWidgetsMap.size());
	}

	private ReleaseWidget createChildReleaseWidgetAt(final Release release, final int index) {
		final ReleaseWidget childItem = releaseWidgetFactory.createReleaseWidget(release);
		releaseContainer.insert(childItem, index);
		releaseWidgetsMap.put(release, childItem);
		return childItem;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ReleasePanelWidget)) return false;
		final ReleasePanelWidget other = (ReleasePanelWidget) obj;

		return releaseWidgetsMap.equals(other.releaseWidgetsMap);
	}
}
