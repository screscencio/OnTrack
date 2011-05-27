package br.com.oncast.ontrack.client.ui.components.releasepanel;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

// TODO Refactor this so that it can be recursive and may work like ScopeTree
public class ReleasePanel extends Composite {

	private final StackLayoutPanel stackLayoutPanel;
	private final ActionExecutionListener actionExecutionListener;
	private ActionExecutionRequestHandler actionHandler;

	public ReleasePanel() {
		initWidget(stackLayoutPanel = new StackLayoutPanel(Unit.PX));

		actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ScopeAction action, final boolean wasRollback) {
				throw new NotImplementedException();
			}
		};
	}

	public void setReleases(final List<Release> releases) {
		for (final Release release : releases) {
			this.add(release);
		}
	}

	public ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener;
	}

	public void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	private void add(final Release release) {
		stackLayoutPanel.add(getReleaseItems(), getHeaderString(release.getDescription()), true, 20);
	}

	/**
	 * Get a string representation of the header that includes some text and style.
	 * 
	 * @param text the header text
	 * @return the header as a string
	 */
	// TODO Incorporate this in a widget defined by a UIBinder interface
	private String getHeaderString(final String text) {
		final HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(0);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final HTML headerText = new HTML(text);
		headerText.setStyleName("cw-StackPanelHeader");
		hPanel.add(headerText);

		return hPanel.getElement().getString();
	}

	// TODO Remove this "testing" code
	private VerticalPanel getReleaseItems() {
		final String[] items = { "0", "1", "2", "3", "4", "5" };
		final VerticalPanel filtersPanel = new VerticalPanel();
		filtersPanel.setSpacing(4);

		for (final String filter : items) {
			filtersPanel.add(new Label(filter));
		}
		return filtersPanel;
	}
}
