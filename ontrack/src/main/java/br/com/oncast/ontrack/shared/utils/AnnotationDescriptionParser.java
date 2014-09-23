package br.com.oncast.ontrack.shared.utils;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;

import java.util.List;

import com.google.gwt.regexp.shared.RegExp;

public class AnnotationDescriptionParser {

	static final RegExp PATTERN = RegExp.compile("@([\\w\\d-]+)", "g");

	public static <T extends HasUUID> String parse(final String text, final List<T> models, final ParseHandler<T> handler) {
		String parsed = text;
		for (final T model : models) {
			parsed = parsed.replaceAll("@" + model.getId(), handler.getReplacement(model));
		}
		return parsed;
	}

	public static interface ParseHandler<T> {

		String getReplacement(T model);

	}

}
