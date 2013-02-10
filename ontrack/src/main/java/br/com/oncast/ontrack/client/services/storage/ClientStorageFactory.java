package br.com.oncast.ontrack.client.services.storage;

import br.com.oncast.ontrack.client.services.admin.OnTrackServerStatistics;
import br.com.oncast.ontrack.client.services.admin.OnTrackServerStatisticsBag;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface ClientStorageFactory extends AutoBeanFactory {

	AutoBean<OnTrackServerStatistics> onTrackServerStatistics();

	AutoBean<OnTrackServerStatisticsBag> onTrackServerStatisticsBag();

}
