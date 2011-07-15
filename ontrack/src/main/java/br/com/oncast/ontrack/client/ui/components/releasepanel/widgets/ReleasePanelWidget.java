package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.utils.deepEquality.IgnoreByDeepEquality;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReleasePanelWidget extends Composite {

	interface ReleasePanelWidgetUiBinder extends UiBinder<Widget, ReleasePanelWidget> {}

	@IgnoreByDeepEquality
	private static ReleasePanelWidgetUiBinder uiBinder = GWT.create(ReleasePanelWidgetUiBinder.class);

	@UiField
	@IgnoreByDeepEquality
	protected VerticalModelWidgetContainer<Release, ReleaseWidget> releaseContainer;

	private Release release;

	// IMPORTANT: This field cannot be 'final' because some tests need to set it to a new value through reflection. Do not remove the 'null' attribution.
	@IgnoreByDeepEquality
	private ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory = null;

	public ReleasePanelWidget() {
		releaseWidgetFactory = new ReleaseWidgetFactory();
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

	@UiFactory
	protected VerticalModelWidgetContainer<Release, ReleaseWidget> createReleaseContainer() {
		return new VerticalModelWidgetContainer<Release, ReleaseWidget>(releaseWidgetFactory, new ModelWidgetContainerListener() {

			@Override
			public void onUpdateComplete(final boolean hasChanged, final boolean hasNewWidgets) {}
		});
	}
}
