package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTagWidget extends Composite implements ModelWidget<TagAssociationMetadata> {

	private static ScopeTagWidgetUiBinder uiBinder = GWT.create(ScopeTagWidgetUiBinder.class);

	interface ScopeTagWidgetUiBinder extends UiBinder<Widget, ScopeTagWidget> {}

	public ScopeTagWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button button;

	public ScopeTagWidget(final TagAssociationMetadata tagAssociation) {
		initWidget(uiBinder.createAndBindUi(this));
		button.setText(tagAssociation.getTag().getDescription());
	}

	@UiHandler("button")
	void onClick(final ClickEvent e) {
		Window.alert("Hello!");
	}

	public void setText(final String text) {
		button.setText(text);
	}

	public String getText() {
		return button.getText();
	}

	@Override
	public boolean update() {
		// FIXME Auto-generated catch block
		return false;
	}

	@Override
	public TagAssociationMetadata getModelObject() {
		// FIXME Auto-generated catch block
		return null;
	}

}
