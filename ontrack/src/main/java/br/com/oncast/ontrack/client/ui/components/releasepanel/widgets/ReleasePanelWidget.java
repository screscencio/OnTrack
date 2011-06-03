package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.LinkedHashMap;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.shared.release.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReleasePanelWidget extends Composite {

	private static ReleasePanelWidgetUiBinder uiBinder = GWT.create(ReleasePanelWidgetUiBinder.class);

	interface ReleasePanelWidgetUiBinder extends UiBinder<Widget, ReleasePanelWidget> {}

	@UiField
	protected VerticalModelWidgetContainer<Release, ReleaseWidget> releaseContainer;

	@UiFactory
	protected VerticalModelWidgetContainer<Release, ReleaseWidget> createReleaseContainer() {
		return new VerticalModelWidgetContainer<Release, ReleaseWidget>(releaseWidgetFactory, new ModelWidgetContainerListener() {

			@Override
			public void onUpdateComplete(final boolean hasChanged, final boolean hasNewWidgets) {}
		});
	}

	private final LinkedHashMap<Release, ReleaseWidget> releaseWidgetsMap;

	private Release release;

	private final ReleaseWidgetFactory releaseWidgetFactory = new ReleaseWidgetFactory();

	public ReleasePanelWidget() {
		releaseWidgetsMap = new LinkedHashMap<Release, ReleaseWidget>();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setRelease(final Release rootRelease) {
		this.release = rootRelease;
		releaseContainer.clear();

		for (final Release childRelease : rootRelease.getChildReleases())
			releaseContainer.createChildModelWidget(childRelease);
	}

	public void update() {
		releaseContainer.update(release.getChildReleases());
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ReleasePanelWidget)) return false;
		final ReleasePanelWidget other = (ReleasePanelWidget) obj;

		return releaseWidgetsMap.equals(other.releaseWidgetsMap);
	}
}
