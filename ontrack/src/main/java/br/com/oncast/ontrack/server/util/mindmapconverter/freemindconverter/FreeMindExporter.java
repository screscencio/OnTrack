package br.com.oncast.ontrack.server.util.mindmapconverter.freemindconverter;

import static br.com.oncast.ontrack.server.util.number.NumberUtils.roundEffort;

import java.io.OutputStream;

import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.FreeMindMap;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.Icon;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.MindNode;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class FreeMindExporter {

	/**
	 * Creates a FreeMind (*.mm) compatible mind map, using a {@link Project} to build the map.
	 * @param project source of data which the map will be created.
	 * @param outputStream the stream in which the data will be written.
	 */
	public static void export(final Project project, final OutputStream outputStream) {
		final FreeMindMap mindMap = FreeMindMap.createNewMap();

		populateMap(project, mindMap);
		save(mindMap, outputStream);
	}

	private static void populateMap(final Project project, final FreeMindMap mindMap) {
		final MindNode root = mindMap.root();

		final Scope scope = project.getProjectScope();
		root.setText(scope.getDescription());

		addLegendTo(root);
		addScopeHierarchyTo(root, scope);
	}

	private static void addLegendTo(final MindNode node) {
		final MindNode legend = appendNodeTo(node, "Legenda", Icon.INFO);
		appendNodeTo(legend, "Associação com entrega", Icon.CALENDAR);
		appendNodeTo(legend, "Declaração de esforço", Icon.LAUNCH);
		appendNodeTo(legend, "Inferência de esforço", Icon.LAUNCH, Icon.WIZARD);
		appendNodeTo(legend, "Declaração de progresso", Icon.HOURGLASS);
	}

	private static void addScopeHierarchyTo(final MindNode project, final Scope scope) {
		final MindNode rootNodeOfScopeHierarchy = appendNodeTo(project, "Árvore de escopo", Icon.LIST);
		populateScopeHierarchy(rootNodeOfScopeHierarchy, scope);
	}

	private static void populateScopeHierarchy(final MindNode node, final Scope scope) {
		addReleaseNodeTo(node, scope);
		addEffortNodeTo(node, scope);
		addProgressNodeTo(node, scope);

		for (final Scope childScope : scope.getChildren()) {
			final MindNode newNode = appendNodeTo(node, childScope.getDescription());
			populateScopeHierarchy(newNode, childScope);
		}
	}

	private static void addReleaseNodeTo(final MindNode node, final Scope scope) {
		if (scope.getRelease() != null) appendNodeTo(node, scope.getRelease().getDescription(), Icon.CALENDAR);
	}

	private static void addEffortNodeTo(final MindNode node, final Scope scope) {
		final Effort effort = scope.getEffort();

		if (effort.hasDeclared()) appendNodeTo(node, Integer.toString(effort.getDeclared()), Icon.LAUNCH);
		if (effort.hasInfered() && (effort.getInfered() > effort.getDeclared())) appendNodeTo(node, roundEffort(effort.getInfered()), Icon.LAUNCH, Icon.WIZARD);
	}

	private static void addProgressNodeTo(final MindNode node, final Scope scope) {
		if (scope.getProgress().hasDeclared()) appendNodeTo(node, scope.getProgress().getDescription(), Icon.HOURGLASS);
	}

	private static MindNode appendNodeTo(final MindNode node, final String text, final Icon... icons) {
		final MindNode newNode = node.appendChild();
		newNode.setText(text);
		for (final Icon icon : icons)
			newNode.addIcon(icon);

		return newNode;
	}

	private static void save(final FreeMindMap map, final OutputStream outputStream) {
		try {
			map.write(outputStream);
			outputStream.close();
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to re-write FreeMind MindMap.", e);
		}
	}

}
