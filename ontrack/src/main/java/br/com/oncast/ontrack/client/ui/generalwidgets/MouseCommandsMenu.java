package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MouseCommandsMenu extends Composite {

	private static MouseActionsMenuUiBinder uiBinder = GWT.create(MouseActionsMenuUiBinder.class);

	interface MouseActionsMenuUiBinder extends UiBinder<Widget, MouseCommandsMenu> {}

	interface Style extends CssResource {}

	@UiField
	protected Style style;

	@UiField
	protected Button button;

	@UiField
	protected CommandMenu menu;

	public MouseCommandsMenu(final List<CommandMenuItem> itens) {
		initWidget(uiBinder.createAndBindUi(this));
		menu.hide();
		menu.setItens(itens);
	}

	@UiHandler("button")
	protected void onClick(final ClickEvent e) {
		menu.show();
	}
}
