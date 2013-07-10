package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ScopeIdAndDescriptionWidget extends Composite {

	private static ScopeIdAndDescriptionWidgetUiBinder uiBinder = GWT.create(ScopeIdAndDescriptionWidgetUiBinder.class);

	interface ScopeIdAndDescriptionWidgetUiBinder extends UiBinder<Widget, ScopeIdAndDescriptionWidget> {}

	@UiField
	SpanElement humanIdLabel;

	@UiField
	SpanElement descriptionLabel;

	private final Scope scope;

	public ScopeIdAndDescriptionWidget(final Scope scope) {
		this.scope = scope;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	public boolean update() {
		return updateHumanId() | updateDescription();
	}

	/**
	 * @return if the humanId was updated.
	 */
	private boolean updateHumanId() {
		final String humanId = ClientServices.getCurrentProjectContext().getHumanId(scope);
		humanIdLabel.setInnerText(humanId);
		if (humanId.isEmpty()) humanIdLabel.getStyle().setDisplay(Display.NONE);
		else humanIdLabel.getStyle().clearDisplay();
		return true;
	}

	/**
	 * @return if the description was updated.
	 */
	private boolean updateDescription() {
		descriptionLabel.setInnerText(scope.getDescription());

		return true;
	}

}
