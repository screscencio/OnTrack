package br.com.oncast.ontrack.client.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.user.ColorProviderService;
import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

import com.google.gwt.event.shared.EventBus;

public class ClientServicesTestUtils {

	public static ClientServicesTestConfiguration configure() throws Exception {
		final Field field = getInstanceField();

		final ClientServices clientServiceProviderMock = mock(ClientServices.class);

		field.set(null, clientServiceProviderMock);

		return new ClientServicesTestConfiguration(clientServiceProviderMock);
	}

	public static void reset() throws Exception {
		getInstanceField().set(null, null);
	}

	private static Field getInstanceField() throws NoSuchFieldException {
		final Field field = ClientServices.class.getDeclaredField("instance");
		field.setAccessible(true);
		return field;
	}

	public static class ClientServicesTestConfiguration {

		private final ClientServices mock;

		public ClientServicesTestConfiguration(final ClientServices mock) {
			this.mock = mock;
		}

		public ClientServicesTestConfiguration mockEventBus() {
			final EventBus bus = mock(EventBus.class);
			when(mock.eventBus()).thenReturn(bus);
			return this;
		}

		public ClientServicesTestConfiguration mockAnnotationService() {
			final DetailService service = mock(DetailService.class);
			when(mock.details()).thenReturn(service);
			return this;
		}

		public ClientServicesTestConfiguration mockEssential() {
			mockEventBus();
			mockAnnotationService();
			mockMembersScopeSelectionService();
			mockForTags();
			mockMetrics();
			return this;
		}

		private ClientServicesTestConfiguration mockMetrics() {
			final ClientMetricsService service = mock(ClientMetricsService.class);
			when(mock.metrics()).thenReturn(service);
			return this;
		}

		private ClientServicesTestConfiguration mockForTags() {
			final ContextProviderService contextProvider = mock(ContextProviderService.class);
			when(mock.contextProvider()).thenReturn(contextProvider);
			final ProjectContext context = mock(ProjectContext.class);
			when(contextProvider.getCurrent()).thenReturn(context);
			when(context.getMetadataList(Mockito.any(HasMetadata.class), Mockito.any(MetadataType.class))).thenReturn(new ArrayList<Metadata>());
			return this;
		}

		private ClientServicesTestConfiguration mockMembersScopeSelectionService() {
			final ColorProviderService service = mock(ColorProviderService.class);
			when(mock.colorProvider()).thenReturn(service);
			return this;
		}

	}

}
