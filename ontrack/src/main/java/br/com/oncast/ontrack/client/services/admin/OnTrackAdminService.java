package br.com.oncast.ontrack.client.services.admin;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.storage.ClientStorageFactory;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OnTrackAdminService {

	private static final ClientStorageFactory FACTORY = GWT.create(ClientStorageFactory.class);

	private final DispatchService dispatchService;

	public OnTrackAdminService(final DispatchService requestDispatchService) {
		this.dispatchService = requestDispatchService;
	}

	public void getStatistics(final AsyncCallback<OnTrackServerStatistics> callback) {
		dispatchService.dispatch(new OnTrackServerStatisticsRequest(), new DispatchCallback<OnTrackServerStatisticsResponse>() {
			@Override
			public void onSuccess(final OnTrackServerStatisticsResponse result) {
				final OnTrackServerStatistics statistics = FACTORY.onTrackServerStatistics().as();
				statistics.setOnlineUsers(toStringSet(result.getOnlineUsers()));
				statistics.setTimestamp(result.getTimestamp());
				callback.onSuccess(statistics);
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	private Set<String> toStringSet(final Set<UUID> onlineUsers) {
		final Set<String> stringSet = new HashSet<String>();
		for (final UUID id : onlineUsers) {
			stringSet.add(id.toString());
		}
		return stringSet;
	}

}
