package br.com.oncast.ontrack.client.services.time;

import java.util.Date;

public class TimeProviderServiceImpl implements TimeProviderService {

	@Override
	public Date now() {
		return new Date();
	}

}
