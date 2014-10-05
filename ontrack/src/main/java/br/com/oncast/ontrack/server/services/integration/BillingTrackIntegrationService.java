package br.com.oncast.ontrack.server.services.integration;

import br.com.oncast.ontrack.server.configuration.Configurations;
import br.com.oncast.ontrack.server.services.integration.bean.ProjectCreationNotificationRequest;
import br.com.oncast.ontrack.server.services.integration.bean.UserInviteRevogueNotificationRequest;
import br.com.oncast.ontrack.server.services.integration.bean.UserInvitedNotificationRequest;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

public class BillingTrackIntegrationService implements IntegrationService {

	private static final Logger LOGGER = Logger.getLogger(BillingTrackIntegrationService.class);

	private final URI baseUri;

	private final ClientConfig config;

	public BillingTrackIntegrationService() {
		final Configurations configurations = Configurations.get();
		baseUri = URI.create(configurations.getIntegrationApiUrl());
		config = new ClientConfig();
		// config.register(new LoggingFilter());
		config.register(new JsonMoxyConfigurationContextResolver());
		config.register(new HttpBasicAuthFilter(configurations.integrationUsername(), configurations.integrationPassword()));
	}

	@Override
	public void onUserInvited(final UUID projectId, final User invitor, final User invitedUser, final Profile profile) {
		final UserInvitedNotificationRequest request = new UserInvitedNotificationRequest(projectId, invitor.getId(), invitedUser.getId(), invitedUser.getEmail(), profile);
		post("userInvite", request, "onUserInvited(" + projectId + ", " + invitor + ", " + invitedUser + ")");
	}

	@Override
	public void onProjectCreated(final ProjectRepresentation project, final User creator) {
		final ProjectCreationNotificationRequest request = new ProjectCreationNotificationRequest(project.getId(), project.getName(), creator.getId());
		post("projectCreation", request, "onProjectCreated(" + project + ", " + creator + ")");
	}

	@Override
	public void onUserInviteRevogued(final ProjectRepresentation project, final User removedUser) {
		final UserInviteRevogueNotificationRequest request = new UserInviteRevogueNotificationRequest(project.getId(), removedUser.getId());
		post("userInviteRevogued", request, "onUserInviteRevogued(" + project + ", " + removedUser + ")");
	}

	private <T> void post(final String path, final T request, final String errorMessage) {
		// final Client client = ClientBuilder.newClient(config);
		// final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("api/notify/" + path);
		// try {
		// final Response response = client.target(uriBuilder).request(MediaType.TEXT_HTML).post(Entity.entity(request, MediaType.APPLICATION_JSON));
		// checkErrors(response);
		// } catch (final Exception e) {
		// LOGGER.error("Could not notify integration server: " + errorMessage, e);
		// }
	}

	private void checkErrors(final Response response) {
		if (response.getStatusInfo().getFamily().equals(Status.Family.SUCCESSFUL)) return;

		final Object entity = response.getEntity();
		if (entity instanceof Throwable) throw new RuntimeException((Throwable) entity);
		else throw new RuntimeException(entity.toString());
	}

	@Provider
	final static class JsonMoxyConfigurationContextResolver implements ContextResolver<MoxyJsonConfig> {
		@Override
		public MoxyJsonConfig getContext(final Class<?> objectType) {
			final MoxyJsonConfig configuration = new MoxyJsonConfig();

			final Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
			namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");

			configuration.setNamespacePrefixMapper(namespacePrefixMapper);
			configuration.setNamespaceSeparator(':');

			return configuration;
		}
	}

}
