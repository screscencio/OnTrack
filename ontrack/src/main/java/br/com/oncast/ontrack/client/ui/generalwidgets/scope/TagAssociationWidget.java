package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TagUpdateAction;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TagAssociationWidget extends Composite implements ModelWidget<TagAssociationMetadata>, ActionExecutionListener {

	private static final int DOUBLE_CLICK_DELAY = 250;

	private static TagAssociationWidgetUiBinder uiBinder = GWT.create(TagAssociationWidgetUiBinder.class);

	interface TagAssociationWidgetUiBinder extends UiBinder<Widget, TagAssociationWidget> {}

	public TagAssociationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label description;

	@UiField
	DivElement upperBg;

	@UiField
	DivElement lowerBg;

	private TagAssociationMetadata association;

	private ScopeTreeItemWidgetEditionHandler editionHandler;

	private final Timer editTimer = new Timer() {
		@Override
		public void run() {
			PopupConfig.configPopup()
					.popup(new TagAssociationWidgetEditMenu(association))
					.alignVertical(VerticalAlignment.TOP, new AlignmentReference(TagAssociationWidget.this, VerticalAlignment.BOTTOM, 2))
					.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(TagAssociationWidget.this, HorizontalAlignment.LEFT))
					.pop();
		}
	};

	public TagAssociationWidget(final TagAssociationMetadata tagAssociation) {
		this(tagAssociation, null);
	}

	public TagAssociationWidget(final TagAssociationMetadata tagAssociation, final ScopeTreeItemWidgetEditionHandler editionHandler) {
		this.association = tagAssociation;
		this.editionHandler = editionHandler;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@UiHandler("root")
	void onRootDoubleClick(final DoubleClickEvent e) {
		doFilter(e);
	}

	@UiHandler("description")
	void onDescriptionDoubleClick(final DoubleClickEvent e) {
		doFilter(e);
	}

	private void doFilter(final DoubleClickEvent e) {
		editTimer.cancel();
		e.stopPropagation();
		e.preventDefault();
		if (editionHandler != null) editionHandler.onFilterByTagRequested(association.getTag().getId());
	}

	@UiHandler("description")
	void onDescriptionClick(final ClickEvent e) {
		editTimer.schedule(DOUBLE_CLICK_DELAY);
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
		description.setText(association.getTag().getDescription());
		updateColor();
		return false;
	}

	private void updateColor() {
		final ColorPack colorPack = association.getTag().getColorPack();
		final Style upperStyle = upperBg.getStyle();
		final Style lowerStyle = lowerBg.getStyle();

		final String bgColor = colorPack.getBackground().toCssRepresentation();
		upperStyle.setBackgroundColor(bgColor);
		lowerStyle.setBackgroundColor(bgColor);

		description.getElement().getStyle().setColor(colorPack.getForeground().toCssRepresentation());
	}

	@Override
	public TagAssociationMetadata getModelObject() {
		return association;
	}

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
			final Set<UUID> inferenceInfluencedScopeSet,
			final boolean isUserAction) {
		if (action instanceof TagUpdateAction && action.getReferenceId().equals(association.getTag().getId())) update();
	};

}
