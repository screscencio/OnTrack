package br.com.oncast.ontrack.shared.exceptions;

import br.com.oncast.ontrack.client.i18n.ServerErrorMessageTranslator;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TranslatableException extends Exception implements IsSerializable {

	private static final long serialVersionUID = 1L;

	private ServerErrorMessageCode code;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	public TranslatableException() {
		super();
	}

	public TranslatableException(final ServerErrorMessageCode code) {
		super(code.name());
		this.code = code;
	}

	@Override
	public String getLocalizedMessage() {
		return ServerErrorMessageTranslator.translate(this.code);
	}

}
