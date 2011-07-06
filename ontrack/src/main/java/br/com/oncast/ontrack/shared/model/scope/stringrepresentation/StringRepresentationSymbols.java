package br.com.oncast.ontrack.shared.model.scope.stringrepresentation;

class StringRepresentationSymbols {

	public static final String RELEASE_SYMBOL = "@";

	public static final String EFFORT_SYMBOL = "#";

	public static final String[] SYMBOLS = { RELEASE_SYMBOL, EFFORT_SYMBOL };

	public static final String getConcatenedSymbols() {
		final StringBuilder str = new StringBuilder();
		for (final String symbol : SYMBOLS) {
			str.append(symbol);
		}
		return str.toString();
	}
}
