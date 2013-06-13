package br.com.oncast.ontrack.shared.metrics;

public enum MetricsCategories {

	PLACE_LOAD("Place Load"),
	CONTEXT_LOAD("Context Load");

	private final String category;

	private MetricsCategories(final String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

}
