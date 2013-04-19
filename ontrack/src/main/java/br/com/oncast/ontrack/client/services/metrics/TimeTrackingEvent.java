package br.com.oncast.ontrack.client.services.metrics;

import java.util.Date;

public class TimeTrackingEvent {

	private long initTime;
	private long endTime;
	private final ClientMetricsServiceImpl service;
	private final String category;
	private final String value;

	/**
	 * It creates an Metric event with the given name,
	 * for convenience it also starts the event
	 * but you can reset the event and start again later
	 * in case you don't want it to start right away
	 * @param string
	 * @param the ClientMetricsServiceImpl that will send the event to metrics server
	 * @param the event name
	 */
	TimeTrackingEvent(final ClientMetricsServiceImpl service, final String category, final String value) {
		this.service = service;
		this.category = category;
		this.value = value;
		reset().start();
	}

	public String getCategory() {
		return category;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Resets the event, this erases the start time and the end time
	 * @return the event itself for convenience
	 */
	public TimeTrackingEvent reset() {
		this.initTime = -1;
		this.endTime = -1;

		return this;
	}

	/**
	 * Starts the event if not already started
	 * This method is safe to call several times without overriding the start time.
	 * @return the event start time in EPOCH
	 */
	public long start() {
		if (initTime == -1) initTime = getCurrentTime();
		return initTime;
	}

	/**
	 * Ends the event if not already ended and sends the metrics to the service
	 * This method is safe to call several times without overriding the end time nor repeating service calls.
	 * @return the event end time in EPOCH
	 */
	public long end() {
		if (endTime == -1) {
			endTime = getCurrentTime();
			service.onTimeTrackingEnd(this);
		}
		return endTime;
	}

	/**
	 * Gets the event current duration without ending it
	 * @return the current event duration in milliseconds
	 */
	public long getCurrentDuration() {
		return getCurrentTime() - initTime;
	}

	/**
	 * Gets the event total duration and ends the event in case it's not ended
	 * @return the total event duration in milliseconds
	 */
	public long getTotalDuration() {
		return end() - initTime;
	}

	private long getCurrentTime() {
		return new Date().getTime();
	}

}
