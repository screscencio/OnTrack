package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TagUpdateAction;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TagWidget extends Composite implements ModelWidget<Tag>, ActionExecutionListener {

	private static TagAssociationWidgetUiBinder uiBinder = GWT.create(TagAssociationWidgetUiBinder.class);

	interface TagAssociationWidgetUiBinder extends UiBinder<Widget, TagWidget> {}

	public TagWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label description;

	@UiField
	DivElement upperBg;

	@UiField
	DivElement lowerBg;

	private Tag tag;

	public TagWidget(final Tag tag) {
		this.tag = tag;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@Override
	protected void onLoad() {
		ClientServices.get().actionExecution().addActionExecutionListener(this);
	}

	@Override
	protected void onUnload() {
		ClientServices.get().actionExecution().removeActionExecutionListener(this);
	}

	@Override
	public boolean update() {
		description.setText(tag.getDescription());
		updateColor();
		return false;
	}

	private void updateColor() {
		final ColorPack colorPack = tag.getColorPack();
		final Style upperStyle = upperBg.getStyle();
		final Style lowerStyle = lowerBg.getStyle();

		final String bgColor = colorPack.getBackground().toCssRepresentation();
		upperStyle.setBackgroundColor(bgColor);
		lowerStyle.setBackgroundColor(bgColor);

		description.getElement().getStyle().setColor(colorPack.getForeground().toCssRepresentation());
	}

	@Override
	public Tag getModelObject() {
		return tag;
	}

	@Override
	public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isUserAction) {
		final ModelAction action = execution.getModelAction();
		if (action instanceof TagUpdateAction && action.getReferenceId().equals(tag.getId())) update();
	};

}
