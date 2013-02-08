package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface CheckListWidgetMessages extends BaseMessages {

	@DefaultMessage("Checklists for ''{0}''")
	@Description("widget title checklists for")
	String checklistFor(String description);

}
