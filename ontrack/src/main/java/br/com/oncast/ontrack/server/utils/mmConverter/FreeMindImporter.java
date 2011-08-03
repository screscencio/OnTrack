package br.com.oncast.ontrack.server.utils.mmConverter;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.oncast.ontrack.server.model.scope.ScopeBuilder;
import br.com.oncast.ontrack.server.utils.mmConverter.abstractions.FreeMindMap;
import br.com.oncast.ontrack.server.utils.mmConverter.abstractions.Icon;
import br.com.oncast.ontrack.server.utils.mmConverter.abstractions.MindNode;
import br.com.oncast.ontrack.shared.model.scope.Scope;

// TODO Review by Lobo - This code has been created by Rodrigo Machado and Jaime and has not yet been reviewed.
public class FreeMindImporter {
	private static Pattern INTEGER_EXTRACTOR = Pattern.compile("\\d+");
	private static Pattern FLOAT_EXTRACTOR = Pattern.compile("(\\d+)(\\.\\d?)?");

	private final FreeMindMap mindMap;

	private FreeMindImporter(final FreeMindMap mindMap) {
		this.mindMap = mindMap;
	}

	public static FreeMindImporter importMapFrom(final File file) {
		return new FreeMindImporter(FreeMindMap.open(file));
	}

	public Scope getScope() {
		final ScopeBuilder builder = ScopeBuilder.scope();
		pullSync(builder, mindMap.root());

		return builder.getScope();
	}

	private static void pullSync(final ScopeBuilder scope, final MindNode node) {
		scope.description(node.getText());
		for (final MindNode childNode : node.getChildren()) {

			if (extractEffort(scope, childNode)) continue;
			if (extractProgress(scope, childNode)) continue;

			final ScopeBuilder childScope = ScopeBuilder.scope();
			pullSync(childScope, childNode);
			scope.add(childScope);
		}
	}

	private static boolean extractProgress(final ScopeBuilder scope, final MindNode childNode) {
		if (childNode.hasIcon(Icon.HOURGLASS)) {
			scope.declaredProgress(extractDeclaredProgress(childNode));
			return true;
		}
		return false;
	}

	private static String extractDeclaredProgress(final MindNode progressNode) {
		return progressNode.getText();
	}

	private static boolean extractEffort(final ScopeBuilder scope, final MindNode childNode) {
		if (childNode.hasIcon(Icon.PENCIL) || childNode.hasIcon(Icon.LAUNCH)) {
			scope.declaredEffort(extractDeclaredEffort(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.DOWN)) {
			scope.topDownEffort(extractCalculatedEffort(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.UP)) {
			scope.bottomUpEffort(extractCalculatedEffort(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.LAUNCH) && childNode.hasIcon(Icon.OK)) {
			scope.accomplishedEffort(extractCalculatedEffort(childNode));
			return true;
		}
		return false;
	}

	private static int extractDeclaredEffort(final MindNode effortNode) {
		final Matcher matcher = INTEGER_EXTRACTOR.matcher(effortNode.getText());
		if (!matcher.find()) return 0;

		return Integer.valueOf(matcher.group());
	}

	private static float extractCalculatedEffort(final MindNode effortNode) {
		final Matcher matcher = FLOAT_EXTRACTOR.matcher(effortNode.getText());
		if (!matcher.find()) return 0.0f;

		return Float.valueOf(matcher.group());
	}

}
