package br.com.oncast.ontrack.client.ui.places.progress.details;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class ProgressDetailMenuWidget extends Composite implements HasWidgets {

	private static ProgressDetailMenuWidgetUiBinder uiBinder = GWT.create(ProgressDetailMenuWidgetUiBinder.class);

	interface ProgressDetailMenuWidgetUiBinder extends UiBinder<Widget, ProgressDetailMenuWidget> {}

	interface ProgressDetailMenuStyle extends CssResource {
		String menuOption();

		String menuOptionActive();
	}

	public interface ProgressDetailMenuListener {
		void onIndexSelected(int index);
	}

	@UiField
	HTMLPanel container;

	@UiField
	ProgressDetailMenuStyle style;

	private final ProgressDetailMenuListener listener;

	private Widget lastSelection;

	public ProgressDetailMenuWidget(final ProgressDetailMenuListener listener) {
		this.listener = listener;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setEnabled(final int index, final boolean enabled) {
		final Widget widget = container.getWidget(index);
		if (!(widget instanceof HasEnabled)) return;

		((HasEnabled) widget).setEnabled(enabled);

		if (!enabled && widget == lastSelection) {
			clearSelection();
			setSelected(0);
			listener.onIndexSelected(0);
		}
	}

	@Override
	public void add(final Widget w) {
		if (!(w instanceof HasEnabled)) throw new IllegalArgumentException("The given widget must implement HasEnabled");
		if (!(w instanceof HasClickHandlers)) throw new IllegalArgumentException("The given widget must implement HasClickHandlers");

		w.addStyleName(style.menuOption());
		container.add(w);

		((HasClickHandlers) w).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				clearSelection();
				setSelected(w);
				listener.onIndexSelected(container.getWidgetIndex(w));
			}

		});
	}

	private void setSelected(final Widget w) {
		lastSelection = w;
		w.addStyleName(style.menuOptionActive());
	}

	private void clearSelection() {
		if (lastSelection == null) return;
		lastSelection.removeStyleName(style.menuOptionActive());
		lastSelection = null;
	}

	@Override
	public void clear() {
		container.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return container.iterator();
	}

	@Override
	public boolean remove(final Widget w) {
		return container.remove(w);
	}

	public void setSelected(final int index) {
		setSelected(container.getWidget(index));
	}

	public int getSelectedIndex() {
		return container.getWidgetIndex(lastSelection);
	}

}
