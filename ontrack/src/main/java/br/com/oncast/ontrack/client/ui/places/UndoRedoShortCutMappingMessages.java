package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface UndoRedoShortCutMappingMessages extends BaseMessages {

	@DefaultMessage("redo")
	@Description("Description for redo shortcut")
	String redo();

	@DefaultMessage("undo")
	@Description("Description for undo shortcut")
	String undo();

}
