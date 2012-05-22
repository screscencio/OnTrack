package br.com.oncast.ontrack.client.ui.places.migration;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.migration.GenerateXmlCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class MigrationPanel extends Composite {

	private static MigrationPanelUiBinder uiBinder = GWT.create(MigrationPanelUiBinder.class);

	interface MigrationPanelUiBinder extends UiBinder<Widget, MigrationPanel> {}

	public MigrationPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button button;

	@UiField
	TextArea resultLabel;

	@UiHandler("button")
	void onClick(final ClickEvent e) {
		resultLabel.setText("Generation XML please wait");
		ClientServiceProvider.getInstance().getMigrationService().generateXml(new GenerateXmlCallback() {

			@Override
			public void onXmlGenerationSuccess(final String text) {
				resultLabel.setText(text);
			}

			@Override
			public void onXmlGenerationFailure(final Throwable caught) {
				resultLabel.setText(caught.getMessage());
			}
		});
	}

}
