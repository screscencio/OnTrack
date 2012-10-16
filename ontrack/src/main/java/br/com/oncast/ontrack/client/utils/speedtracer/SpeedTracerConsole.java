package br.com.oncast.ontrack.client.utils.speedtracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class SpeedTracerConsole {

	public static native void log(String msg) /*-{
		var logger = $wnd.console;
		if (logger && logger.markTimeline) {
			logger.markTimeline(msg);
		}
	}-*/;

	public static SpeedTracerEvent start(final String eventName, final String... data) {
		if (data.length % 2 != 0) throw new IllegalArgumentException("data lenth should be pair, key folloing by value");

		return new SpeedTracerEvent(eventName, Arrays.asList(data));
	}

	public static class SpeedTracerEvent {

		private static int counter = 0;

		private final String name;
		private final long initTime;
		private final List<String> data;
		private final int identationLevel;

		private SpeedTracerEvent(final String name, final List<String> data) {
			this.name = name;
			this.initTime = new Date().getTime();
			this.data = data;
			this.identationLevel = ++counter;
			log(getName() + getDataString());
		}

		private String getDataString() {
			if (data.isEmpty()) return "";

			final StringBuilder builder = new StringBuilder();
			builder.append("[");
			final List<String> dataTuples = new ArrayList<String>();
			for (int i = 0; i < data.size(); i += 2) {
				dataTuples.add(Joiner.on(": ").join(data.get(i), data.get(i + 1)));
			}
			builder.append(Joiner.on(", ").join(dataTuples));
			builder.append("]");
			return builder.toString();
		}

		private String getName() {
			return identationLevel > 1 ? identationLevel + " " + name : name;
		}

		public void end() {
			counter--;
			log(getName() + ": " + getDuration() + " ms");
		}

		JSONObject toJson() {
			final JSONObject json = new JSONObject();
			json.put("type", new JSONNumber(-2));
			json.put("typeName", new JSONString(name));
			json.put("color", new JSONString("red"));
			json.put("time", new JSONNumber(initTime));
			json.put("duration", new JSONNumber(getDuration()));

			final JSONObject jsonData = new JSONObject();
			for (int i = 0; i < data.size(); i += 2) {
				jsonData.put(data.get(i), new JSONString(data.get(i + 1)));
			}
			json.put("data", jsonData);

			return json;
		}

		private double getDuration() {
			return new Date().getTime() - initTime;
		}

		private static class Counter {
			private int count = 0;

			public int increase() {
				return ++count;
			}

			public void decrease() {
				count -= 1;
			}

		}
	}
}
