package br.com.oncast.ontrack.client.ui.components.releasepanel;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelItemWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidgetFactory;
import br.com.oncast.ontrack.shared.release.Release;

public class ReleaseWidgetMock extends ReleasePanelItemWidget {

	private StyleImplementation styleImplementation;

	private final class StyleImplementation implements Style {
		@Override
		public String getName() {
			return "name";
		}

		@Override
		public String getText() {
			return "text";
		}

		@Override
		public boolean ensureInjected() {
			return false;
		}

		@Override
		public String invisibleBodyContainer() {
			return "invisibleBodyContainer";
		}

		@Override
		public String headerContainerStateImageOpened() {
			return "headerContainerStateImageOpened";
		}

		@Override
		public String headerContainerStateImageClosed() {
			return "headerContainerStateImageClosed";
		}
	}

	public ReleaseWidgetMock(final Release release, final ReleaseWidgetFactory releaseWidgetFactory) {
		super(release, releaseWidgetFactory);
	}

	@Override
	protected Style getStyle() {
		if (styleImplementation != null) return styleImplementation;
		return styleImplementation = new StyleImplementation();
	}
}
