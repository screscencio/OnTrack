package br.com.oncast.ontrack.shared.metrics;

public enum MetricsCategories {

	PLACE_LOAD("Place Load"), CONTEXT_LOAD("Context Load"), CLIENT_CONNECTION_STATUS("Connection Status"), ACTIONS_DISPATCH("Actions Dispatch"), ACTIONS_FETCH("Actions Fetch");

	private final String category;

	private MetricsCategories(final String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

}
