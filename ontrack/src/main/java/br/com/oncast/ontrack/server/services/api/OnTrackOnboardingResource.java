package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.configuration.Configurations;
import br.com.oncast.ontrack.server.services.api.bean.OnboardingApiRequest;
import br.com.oncast.ontrack.shared.model.user.Profile;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/onboarding")
public class OnTrackOnboardingResource {

	@POST
	@Path("/user")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String onboarding(final OnboardingApiRequest request) {
		final String accessUrl = ServerServiceProvider.getInstance().getBusinessLogic().createUser(request.getEmail(), Profile.ACCOUNT_MANAGER).toString();
		return Configurations.get().getApplicationBaseUrl() + "/onboarding/access/" + accessUrl;
	}

}
