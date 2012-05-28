package br.com.oncast.ontrack.acceptance;

public interface ConditionVerifierMomentSpecification {

	public abstract void now();

	public abstract void during(final long millis);

}