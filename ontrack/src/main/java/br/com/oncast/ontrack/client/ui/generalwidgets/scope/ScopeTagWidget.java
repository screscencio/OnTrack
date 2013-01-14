package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTagWidget extends Composite implements ModelWidget<TagAssociationMetadata> {

	private static final int DURATION = 500;

	private static final int COLOR_EDIT_WIDTH = 17;

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
		Window.alert("HA!");
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

		private int endWidth = -1;
		private int startWidth = -1;

		@Override
		protected void onUpdate(final double progress) {
			colorEdit.setWidth((startWidth + (progress * (endWidth - startWidth))) + "px");
		}

		@Override
		protected void onStart() {
			colorEdit.setVisible(true);
		}

		@Override
		protected void onComplete() {
			colorEdit.setVisible(endWidth != 0);
		}

		public void show() {
			if (startWidth == 0) return;

			startWidth = 0;
			endWidth = COLOR_EDIT_WIDTH;
			run(DURATION);
		}

		public void hide() {
			if (endWidth == 0) return;

			startWidth = COLOR_EDIT_WIDTH;
			endWidth = 0;
			run(DURATION);
		}

	};

}
