package br.com.oncast.ontrack.client.ui.generalwidgets.alerting;

import br.com.oncast.ontrack.shared.model.color.Color;

public enum AlertType {
	WARNING("icon-exclamation-sign", Color.ORANGE_LIGHT),
	ERROR("icon-remove-sign", Color.RED),
	SUCCESS("icon-ok-sign", Color.DARK_GREEN),
	INFO("icon-info-sign", Color.INFO);

	private final String resource;
	private final Color color;

	private AlertType(final String resource, final Color color) {
		this.resource = resource;
		this.color = color;
	}

	String getIconClass() {
		return resource;
	}

	public Color getColor() {
		return color;
	}
}
