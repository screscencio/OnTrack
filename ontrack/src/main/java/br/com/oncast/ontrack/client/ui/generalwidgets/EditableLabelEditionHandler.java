package br.com.oncast.ontrack.client.ui.generalwidgets;

public interface EditableLabelEditionHandler {

	boolean onEditionRequest(String text);

	void onEditionExit(boolean canceledEdition);

	void onEditionStart();

}
