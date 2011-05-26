package br.com.oncast.ontrack.client.ui.component.releasepanel;

import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReleasePanel extends StackLayoutPanel {

	@UiConstructor
	public ReleasePanel(final Unit unit) {
		super(unit);
	}

	public void setReleases(final List<Release> releases) {
		for (final Release release : releases) {
			this.add(release);
		}
	}

	public void add(final Release release) {
		super.add(getReleaseItems(), getHeaderString(release.getDescription()), true, 20);
	}

	/**
	 * Get a string representation of the header that includes some text and style.
	 * 
	 * @param text the header text
	 * @return the header as a string
	 */
	private String getHeaderString(final String text) {
		final HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(0);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final HTML headerText = new HTML(text);
		headerText.setStyleName("cw-StackPanelHeader");
		hPanel.add(headerText);

		return hPanel.getElement().getString();
	}

	private VerticalPanel getReleaseItems() {
		final String[] items = { "0", "1", "2", "3", "4", "5" };
		final VerticalPanel filtersPanel = new VerticalPanel();
		filtersPanel.setSpacing(4);

		for (final String filter : items) {
			filtersPanel.add(new Label(filter));
		}
		return filtersPanel;
	}
}
