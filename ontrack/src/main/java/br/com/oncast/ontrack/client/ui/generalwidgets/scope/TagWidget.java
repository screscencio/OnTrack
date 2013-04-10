package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TagUpdateAction;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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
		ClientServiceProvider.get().actionExecution().addActionExecutionListener(this);
	}

	@Override
	protected void onUnload() {
		ClientServiceProvider.get().actionExecution().removeActionExecutionListener(this);
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
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
			final Set<UUID> inferenceInfluencedScopeSet,
			final boolean isUserAction) {
		if (action instanceof TagUpdateAction && action.getReferenceId().equals(tag.getId())) update();
	};

}
