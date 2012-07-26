package br.com.oncast.ontrack.client.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import br.com.oncast.ontrack.client.services.annotations.AnnotationService;

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
			final AnnotationService service = mock(AnnotationService.class);
			when(mock.getAnnotationService()).thenReturn(service);
			return this;
		}

		public ClientServiceProviderTestConfiguration mockEssential() {
			mockEventBus();
			mockAnnotationService();
			return this;
		}

	}

}
