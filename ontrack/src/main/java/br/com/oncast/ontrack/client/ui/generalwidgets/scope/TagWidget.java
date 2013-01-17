package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TagUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TagWidget extends Composite implements ModelWidget<Tag>, ActionExecutionListener {

	private static TagWidgetUiBinder uiBinder = GWT.create(TagWidgetUiBinder.class);

	interface TagWidgetUiBinder extends UiBinder<Widget, TagWidget> {}

	@UiField
	Label description;

	@UiField
	HTMLPanel container;

	private final Tag tag;

	public TagWidget(final Tag tag) {
		this.tag = tag;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@Override
	protected void onLoad() {
		ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(this);
	}

	@Override
	protected void onUnload() {
		ClientServiceProvider.getInstance().getActionExecutionService().removeActionExecutionListener(this);
	}

	@Override
	public boolean update() {
		description.setText(tag.getDescription());
		updateColor();
		return false;
	}

	private void updateColor() {
		final Style style = container.getElement().getStyle();

		style.setBackgroundColor(tag.getColorPack().getBackground().toCssRepresentation());
		style.setColor(tag.getColorPack().getForeground().toCssRepresentation());
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
