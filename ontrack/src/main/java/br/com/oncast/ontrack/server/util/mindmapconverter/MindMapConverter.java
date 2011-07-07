package br.com.oncast.ontrack.server.util.mindmapconverter;

import java.io.File;

import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.FreeMindMap;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemindconverter.FreeMindConverter;
import br.com.oncast.ontrack.server.util.mindmapconverter.scope.ScopePrinter;
import br.com.oncast.ontrack.shared.model.scope.Scope;

/**
 * A mind map converter that reads a *.mm file and convert it into a scope hierarchy.
 */
public class MindMapConverter {

	/**
	 * Reads a *.mm file and convert it into a scope hierarchy.
	 * @param file A mind map file to be loaded, in *.mm format.
	 * @return A scope hierarchy loaded from the map.
	 */
	public static Scope convert(final File file) {
		final FreeMindMap mindMap = FreeMindMap.open(file);
		final FreeMindConverter converter = FreeMindConverter.interpret(mindMap);

		final Scope scope = converter.getScope();
		ScopePrinter.print(scope);

		return scope;
	}
}