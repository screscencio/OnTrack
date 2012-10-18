package br.com.oncast.ontrack.server.services.exportImport.freemind;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.oncast.ontrack.server.model.scope.ScopeBuilder;
import br.com.oncast.ontrack.server.services.exportImport.freemind.abstractions.FreeMindMap;
import br.com.oncast.ontrack.server.services.exportImport.freemind.abstractions.Icon;
import br.com.oncast.ontrack.server.services.exportImport.freemind.abstractions.MindNode;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// TODO Review by Lobo - This code has been created by Rodrigo Machado and Jaime and has not yet been reviewed.
public class FreeMindImporter {
	private static Pattern FLOAT_EXTRACTOR = Pattern.compile("(\\d+)(\\.\\d?)?");

	private final FreeMindMap mindMap;

	private FreeMindImporter(final FreeMindMap mindMap) {
		this.mindMap = mindMap;
	}

	public static FreeMindImporter importMapFrom(final File file) {
		return new FreeMindImporter(FreeMindMap.open(file));
	}

	public Scope getScope() {
		final ScopeBuilder builder = ScopeBuilder.scope(getUser(), new Date());
		pullSync(builder, mindMap.root());

		return builder.getScope();
	}

	private static User getUser() {
		return new User(new UUID(), "robot@ontrack.com");
	}

	private static void pullSync(final ScopeBuilder scope, final MindNode node) {
		scope.description(node.getText());
		for (final MindNode childNode : node.getChildren()) {

			if (extractEffort(scope, childNode)) continue;
			if (extractValue(scope, childNode)) continue;
			if (extractProgress(scope, childNode)) continue;

			final ScopeBuilder childScope = ScopeBuilder.scope(getUser(), new Date());
			pullSync(childScope, childNode);
			scope.add(childScope);
		}
	}

	private static boolean extractProgress(final ScopeBuilder scope, final MindNode childNode) {
		if (childNode.hasIcon(Icon.HOURGLASS)) {
			scope.declaredProgress(extractDeclaredProgress(childNode), getUser(), new Date());
			return true;
		}
		return false;
	}

	private static String extractDeclaredProgress(final MindNode progressNode) {
		return progressNode.getText();
	}

	private static boolean extractValue(final ScopeBuilder scope, final MindNode childNode) {
		if (childNode.hasIcon(Icon.LAUNCH) && childNode.hasIcon(Icon.OK) && childNode.hasIcon(Icon.STAR)) {
			scope.accomplishedValue(extractCalculated(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.PENCIL) && childNode.hasIcon(Icon.STAR)) {
			scope.declaredValue(extractDeclared(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.DOWN) && childNode.hasIcon(Icon.STAR)) {
			scope.topDownValue(extractCalculated(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.UP) && childNode.hasIcon(Icon.STAR)) {
			scope.bottomUpValue(extractCalculated(childNode));
			return true;
		}
		return false;
	}

	private static boolean extractEffort(final ScopeBuilder scope, final MindNode childNode) {
		if (childNode.hasIcon(Icon.LAUNCH) && childNode.hasIcon(Icon.OK) && !childNode.hasIcon(Icon.STAR)) {
			scope.accomplishedEffort(extractCalculated(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.PENCIL) && !childNode.hasIcon(Icon.STAR)) {
			scope.declaredEffort(extractDeclared(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.DOWN) && !childNode.hasIcon(Icon.STAR)) {
			scope.topDownEffort(extractCalculated(childNode));
			return true;
		}
		else if (childNode.hasIcon(Icon.UP) && !childNode.hasIcon(Icon.STAR)) {
			scope.bottomUpEffort(extractCalculated(childNode));
			return true;
		}
		return false;
	}

	private static float extractDeclared(final MindNode effortNode) {
		final Matcher matcher = FLOAT_EXTRACTOR.matcher(effortNode.getText());
		if (!matcher.find()) return 0.0f;

		return Float.valueOf(matcher.group());
	}

	private static float extractCalculated(final MindNode effortNode) {
		final Matcher matcher = FLOAT_EXTRACTOR.matcher(effortNode.getText());
		if (!matcher.find()) return 0.0f;

		return Float.valueOf(matcher.group());
	}

}
