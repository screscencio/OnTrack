package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RichTextArea;

public class UserMentionCommandItemFactory implements CustomCommandMenuItemFactory {

	private final PopupConfig config;
	private final RichTextArea textArea;
	private int selectionIndex;

	public UserMentionCommandItemFactory(final PopupConfig config, final RichTextArea textArea) {
		this.config = config;
		this.textArea = textArea;
		this.selectionIndex = 3; // IMPORTANT somehow this works
	}

	public CommandMenuItem createItem(final User user) {
		return new SimpleCommandMenuItem(user.getName(), new Command() {
			@Override
			public void execute() {
				appendText("<span data-user-id=\"" + user.getId().toString() + "\" class=\"user-mention\">" + user.getName() + "</span>&nbsp;");
			}
		});
	}

	@Override
	public CommandMenuItem createCustomItem(final String inputText) {
		return new SimpleCommandMenuItem("Não mencionar", new Command() {
			@Override
			public void execute() {
				appendText(inputText);
			}
		});
	}

	@Override
	public boolean shouldPrioritizeCustomItem() {
		return false;
	}

	@Override
	public String getNoItemText() {
		return "Usuário não encontrado";
	}

	private void appendText(final String text) {
		final String newText = textArea.getHTML().replaceAll("@[^@]*$", "@") + text;
		textArea.setHTML(newText);
		try {
			setSelectionRange(textArea.getElement(), selectionIndex);
			selectionIndex += 2; // IMPORTANT somehow this works
		} catch (final Exception e) {
			selectionIndex = 3;
			setSelectionRange(textArea.getElement(), selectionIndex);
		}

		textArea.setFocus(true);
		config.hidePopup();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {

			}
		});
	}

	public native void setSelectionRange(Element elem, int pos) /*-{
		var selection = null, range2 = null;
		var iframeWindow = elem.contentWindow;
		var iframeDocument = iframeWindow.document;

		selection = iframeWindow.getSelection();
		range2 = selection.getRangeAt(0);

		//create new range
		var range = iframeDocument.createRange();
		range.setStart(selection.anchorNode, pos);
		range.setEnd(selection.anchorNode, pos);

		//remove the old range and add the newly created range
		if (selection.removeRange) { // Firefox, Opera, IE after version 9
			selection.removeRange(range2);
		} else {
			if (selection.removeAllRanges) { // Safari, Google Chrome
				selection.removeAllRanges();
			}
		}
		selection.addRange(range);
	}-*/;

}
