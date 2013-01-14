package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.tag.Tag;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTagWidget extends Composite implements ModelWidget<TagAssociationMetadata> {

	private static final int DURATION = 500;

	private static ScopeTagWidgetUiBinder uiBinder = GWT.create(ScopeTagWidgetUiBinder.class);

	interface ScopeTagWidgetUiBinder extends UiBinder<Widget, ScopeTagWidget> {}

	public ScopeTagWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label description;

	@UiField
	HTMLPanel container;

	@UiField
	FocusPanel colorEdit;

	private TagAssociationMetadata association;

	private ScopeTreeItemWidgetEditionHandler editionHandler;

	private final ScopeTagWidgetAnimation animation = new ScopeTagWidgetAnimation();

	private final Timer hideTimer = new Timer() {
		@Override
		public void run() {
			animation.hide();
		}
	};

	public ScopeTagWidget(final TagAssociationMetadata tagAssociation, final ScopeTreeItemWidgetEditionHandler editionHandler) {
		this.association = tagAssociation;
		this.editionHandler = editionHandler;
		initWidget(uiBinder.createAndBindUi(this));
		update();
		animation.hide();
	}

	@UiHandler("root")
	void onRootMouseOver(final MouseMoveEvent e) {
		hideTimer.cancel();
		animation.show();
	}

	@UiHandler("root")
	void onRootMouseOut(final MouseOutEvent e) {
		hideTimer.cancel();
		hideTimer.schedule(DURATION);
	}

	@UiHandler("colorEdit")
	void onClick(final ClickEvent e) {
		PopupConfig.configPopup()
				.popup(new ScopeTagWidgetEditMenu(association))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(this, VerticalAlignment.BOTTOM))
				.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(colorEdit, HorizontalAlignment.LEFT))
				.pop();
	}

	@Override
	public boolean update() {
		description.setText(association.getTag().getDescription());
		updateColor();
		return false;
	}

	private void updateColor() {
		final Tag tag = association.getTag();
		final Style style = container.getElement().getStyle();

		style.setBackgroundColor(tag.getColorPack().getBackground().toCssRepresentation());
		style.setColor(tag.getColorPack().getForeground().toCssRepresentation());
	}

	@Override
	public TagAssociationMetadata getModelObject() {
		return association;
	}

	private class ScopeTagWidgetAnimation extends Animation {

		private static final double HIDDEN = 1.0;

		private double endOpacity = -1;
		private double startOpacity = -1;

		@Override
		protected void onUpdate(final double progress) {
			colorEdit.getElement().getStyle().setOpacity(startOpacity + (progress * (endOpacity - startOpacity)));
		}

		@Override
		protected void onStart() {
			colorEdit.getElement().getStyle().setOpacity(startOpacity);
			colorEdit.setVisible(true);
		}

		@Override
		protected void onComplete() {
			colorEdit.setVisible(endOpacity != 0);
		}

		public void show() {
			if (startOpacity == 0) return;

			startOpacity = 0;
			endOpacity = HIDDEN;
			run(DURATION);
		}

		public void hide() {
			if (endOpacity == 0) return;

			startOpacity = HIDDEN;
			endOpacity = 0;
			run(DURATION);
		}

	};

}
