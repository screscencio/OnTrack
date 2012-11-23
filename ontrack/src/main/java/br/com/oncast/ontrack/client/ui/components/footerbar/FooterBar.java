package br.com.oncast.ontrack.client.ui.components.footerbar;

import br.com.oncast.ontrack.client.ui.generalwidgets.HorizontalScrollMover;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FooterBar extends Composite {

	private static FooterBarUiBinder uiBinder = GWT.create(FooterBarUiBinder.class);

	interface FooterBarUiBinder extends UiBinder<Widget, FooterBar> {}

	@UiField
	HTMLPanel scroll;

	@UiField
	FocusPanel leftScrollArea;

	@UiField
	FocusPanel rightScrollArea;

	@UiField
	Button feedback;

	private HorizontalScrollMover mover;

	public FooterBar() {
		initWidget(uiBinder.createAndBindUi(this));

		mover = new HorizontalScrollMover(scroll.getElement());
	}

	@UiHandler("leftScrollArea")
	void onLeftScrollAreaOver(final MouseOverEvent event) {
		mover.moveLeft();
	}

	@UiHandler("leftScrollArea")
	void onLeftScrollAreaOut(final MouseOutEvent event) {
		mover.cancel();
	}

	@UiHandler("rightScrollArea")
	void onRightScrollAreaOver(final MouseOverEvent event) {
		mover.moveRight();
	}

	@UiHandler("rightScrollArea")
	void onRightScrollAreaOut(final MouseOutEvent event) {
		mover.cancel();
	}

	@UiHandler("feedback")
	void onClick(final ClickEvent event) {
		PopupConfig.configPopup()
				.popup(new FeedbackPopup())
				.onClose(new PopupCloseListener() {
					@Override
					public void onHasClosed() {
						feedback.setVisible(true);
					}
				})
				.pop();
		feedback.setVisible(false);
	}

	public FooterBar(final String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
