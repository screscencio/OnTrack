package br.com.oncast.ontrack.server.util.mindmapconverter.freemindconverter;

import static br.com.oncast.ontrack.server.util.number.NumberUtils.roundEffort;

import java.io.File;
import java.io.FileOutputStream;

import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.FreeMindMap;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.Icon;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.MindNode;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class FreeMindExporter {

	public static void export(final Project project, final File file) {
		final FreeMindMap mindMap = FreeMindMap.createNewMap();

		populateMap(project, mindMap);
		save(mindMap, file);
	}

	private static void populateMap(final Project project, final FreeMindMap mindMap) {
		final MindNode root = mindMap.root();

		final Scope scope = project.getProjectScope();
		root.setText(scope.getDescription());

		addLegendTo(root);
		addScopeHierarchyTo(root, scope);
	}

	private static void addScopeHierarchyTo(final MindNode project, final Scope scope) {
		final MindNode rootNodeOfScopeHierarchy = appendNodeTo(project, "Árvore de escopo", Icon.LIST);
		populateScopeHierarchy(rootNodeOfScopeHierarchy, scope);
	}

	private static void populateScopeHierarchy(final MindNode rootNode, final Scope rootScope) {
		final Effort effort = rootScope.getEffort();

		if (effort.hasDeclared()) appendNodeTo(rootNode, Integer.toString(effort.getDeclared()), Icon.LAUNCH);
		if (effort.hasInfered() && (effort.getInfered() > effort.getDeclared())) appendNodeTo(rootNode, roundEffort(effort.getInfered()), Icon.LAUNCH,
				Icon.WIZARD);
		if (rootScope.getProgress().hasDeclared()) appendNodeTo(rootNode, rootScope.getProgress().getDescription(), Icon.HOURGLASS);

		for (final Scope childScope : rootScope.getChildren()) {
			final MindNode newNode = appendNodeTo(rootNode, childScope.getDescription());
			populateScopeHierarchy(newNode, childScope);
		}
	}

	private static void addLegendTo(final MindNode node) {
		final MindNode legend = appendNodeTo(node, "Legenda", Icon.INFO);
		appendNodeTo(legend, "Associação com entrega", Icon.CALENDAR);
		appendNodeTo(legend, "Declaração de esforço", Icon.LAUNCH);
		appendNodeTo(legend, "Inferência de esforço", Icon.LAUNCH, Icon.WIZARD);
		appendNodeTo(legend, "Declaração de progresso", Icon.HOURGLASS);
	}

	private static MindNode appendNodeTo(final MindNode node, final String text, final Icon... icons) {
		final MindNode newNode = node.appendChild();
		newNode.setText(text);
		for (final Icon icon : icons)
			newNode.addIcon(icon);

		return newNode;
	}

	private static void save(final FreeMindMap map, final File file) {
		try {
			final FileOutputStream stream = new FileOutputStream(file);
			map.write(stream);
			stream.close();
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to re-write FreeMind MindMap.", e);
		}
	}

}
