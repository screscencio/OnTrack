package br.com.oncast.ontrack.client.ui.generalwidgets.instructions;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface InstructionWidgetMessages extends BaseMessages {

	@DefaultMessage("dismiss")
	@Description("text to dismiss intructionWidget")
	String dismiss();

	@DefaultMessage("click for more info")
	@Description("help text to show more info")
	String moreInfo();

	@DefaultMessage("click for less info")
	@Description("help text to show less info")
	String lessInfo();

}
