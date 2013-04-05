package br.com.oncast.ontrack.client.ui.generalwidgets;

public interface CustomCommandMenuItemFactory {

	CommandMenuItem createCustomItem(String inputText);

	boolean shouldPrioritizeCustomItem();

	/**
	 * @return Text to be shown when there is no matching item, if you don't want to show any text return null.<br>
	 *         Note if the returned text is an empty text it will show an empty text.
	 */
	String getNoItemText();
}
