package br.com.oncast.ontrack.server.util.mindmapconverter.freemindconverter;

import static br.com.oncast.ontrack.server.util.mindmapconverter.scope.ScopeBuilder.scope;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.FreeMindMap;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.Icon;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.MindNode;
import br.com.oncast.ontrack.server.util.mindmapconverter.scope.ScopeBuilder;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class FreeMindConverter {
	private static Pattern INTEGER_EXTRACTOR = Pattern.compile("\\d+");

	private final FreeMindMap mindMap;

	private FreeMindConverter(final FreeMindMap mindMap) {
		this.mindMap = mindMap;
	}

	public static FreeMindConverter interpret(final FreeMindMap mindMap) {
		return new FreeMindConverter(mindMap);
	}

	public Scope getScope() {
		final ScopeBuilder builder = scope();
		pullSync(builder, mindMap.root());

		return builder.getScope();
	}

	private static void pullSync(final ScopeBuilder scope, final MindNode node) {
		int declaredEffort = 0;
		float calculatedEffort = 0;
		boolean effortDeclared = false;
		boolean effortCalculated = false;

		scope.description(node.getText());
		for (final MindNode childNode : node.getChildren()) {
			if (childNode.hasIcon(Icon.PENCIL)) {
				effortDeclared = true;
				declaredEffort += extractEffort(childNode);
				continue;
			}
			else if (childNode.hasIcon(Icon.IDEA)) {
				effortCalculated = true;
				calculatedEffort += extractEffort(childNode);
				continue;
			}

			final ScopeBuilder childScope = scope();
			pullSync(childScope, childNode);
			scope.add(childScope);
		}

		if (effortDeclared) scope.declaredEffort(declaredEffort);
		if (effortCalculated) scope.calculatedEffort(calculatedEffort);
	}

	private static float extractEffort(final MindNode effortNode) {
		final Matcher matcher = INTEGER_EXTRACTOR.matcher(effortNode.getText());
		if (!matcher.find()) return 0;

		return Integer.valueOf(matcher.group());
	}
}
