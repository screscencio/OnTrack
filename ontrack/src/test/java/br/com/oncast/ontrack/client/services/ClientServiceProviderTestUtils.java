package br.com.oncast.ontrack.client.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.client.services.user.ColorProviderService;
import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

import com.google.gwt.event.shared.EventBus;

public class ClientServiceProviderTestUtils {

	public static ClientServiceProviderTestConfiguration configure() throws Exception {
		final Field field = getInstanceField();

		final ClientServiceProvider clientServiceProviderMock = mock(ClientServiceProvider.class);

		field.set(null, clientServiceProviderMock);

		return new ClientServiceProviderTestConfiguration(clientServiceProviderMock);
	}

	public static void reset() throws Exception {
		getInstanceField().set(null, null);
	}

	private static Field getInstanceField() throws NoSuchFieldException {
		final Field field = ClientServiceProvider.class.getDeclaredField("instance");
		field.setAccessible(true);
		return field;
	}

	public static class ClientServiceProviderTestConfiguration {

		private final ClientServiceProvider mock;

		public ClientServiceProviderTestConfiguration(final ClientServiceProvider mock) {
			this.mock = mock;
		}

		public ClientServiceProviderTestConfiguration mockEventBus() {
			final EventBus bus = mock(EventBus.class);
			when(mock.getEventBus()).thenReturn(bus);
			return this;
		}

		public ClientServiceProviderTestConfiguration mockAnnotationService() {
			final DetailService service = mock(DetailService.class);
			when(mock.getAnnotationService()).thenReturn(service);
			return this;
		}

		public ClientServiceProviderTestConfiguration mockEssential() {
			mockEventBus();
			mockAnnotationService();
			mockMembersScopeSelectionService();
			mockForTags();
			return this;
		}

		private ClientServiceProviderTestConfiguration mockForTags() {
			final ContextProviderService contextProvider = mock(ContextProviderService.class);
			when(mock.getContextProviderService()).thenReturn(contextProvider);
			final ProjectContext context = mock(ProjectContext.class);
			when(contextProvider.getCurrent()).thenReturn(context);
			when(context.getMetadataList(Mockito.any(HasMetadata.class), Mockito.any(MetadataType.class))).thenReturn(new ArrayList<Metadata>());
			return this;
		}

		private ClientServiceProviderTestConfiguration mockMembersScopeSelectionService() {
			final ColorProviderService service = mock(ColorProviderService.class);
			when(mock.getColorProviderService()).thenReturn(service);
			return this;
		}

	}

}
