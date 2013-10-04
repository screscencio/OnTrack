package br.com.oncast.ontrack.shared.model.action;

import com.google.gwt.core.client.GWT;
import com.kfuntak.gwt.json.serialization.client.Serializer;

public class ModelActionSerializer {

	private static final Serializer SERIALIZER = (Serializer) GWT.create(Serializer.class);

	private static final String CLASS_NAME_SEPARATOR = "__#__";

	public static String serialize(final ModelAction action) {
		final String json = SERIALIZER.serialize(action);
		return action.getClass().getName() + CLASS_NAME_SEPARATOR + json;
	}

	public static ModelAction deSerialize(final String s) {
		if (s == null || !s.contains(CLASS_NAME_SEPARATOR)) return null;

		final int separatorIndex = s.indexOf(CLASS_NAME_SEPARATOR);
		final String className = s.substring(0, separatorIndex);
		final String json = s.substring(separatorIndex + CLASS_NAME_SEPARATOR.length());
		return (ModelAction) SERIALIZER.deSerialize(json, className);
	}

}
