package br.com.oncast.ontrack.shared.messageCode;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface BaseMessageCode<T extends BaseMessages> {

	String selectMessage(T messages, final String... args);

}
