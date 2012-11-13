package br.com.oncast.ontrack.client.ui.generalwidgets.instructions;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface WarnningTipWidgetMessages extends BaseMessages {

	@DefaultMessage("dismiss")
	@Description("text to dismiss the tip wiget")
	String dismiss();

	@DefaultMessage("click for sugestions")
	@Description("help text to show sugestions")
	String sugestions();

	@DefaultMessage("back")
	@Description("back")
	String back();

}
