package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface UndoWarningMessages extends BaseMessages {

	@Description("undo warning undo button text")
	@DefaultMessage("undo")
	String undo();

	@Description("undo message when ScopeRemoveAction was executed")
	@DefaultMessage("The scope has been removed!")
	String scopeRemove();

	@Description("undo message when ReleaseRemoveAction was executed")
	@DefaultMessage("The release has been removed!")
	String releaseRemove();

}
