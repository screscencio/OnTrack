package br.com.oncast.ontrack.client.ui.places.progress.details;

import br.com.oncast.ontrack.client.ui.generalwidgets.DescriptionWidget;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressDetailPanel extends Composite {

	private static ProgressDetailPanelUiBinder uiBinder = GWT.create(ProgressDetailPanelUiBinder.class);

	@UiField(provided = true)
	protected DescriptionWidget descriptionWidget;

	@UiField
	protected DeckPanel deckPanel;

	interface ProgressDetailPanelUiBinder extends UiBinder<Widget, ProgressDetailPanel> {}

	public ProgressDetailPanel(final Release release) {
		descriptionWidget = new DescriptionWidget(release);
		initWidget(uiBinder.createAndBindUi(this));
		deckPanel.showWidget(0);
	}

	public DescriptionWidget getDescriptionWidget() {
		return descriptionWidget;
	}
}
