package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.OnboardingApiRequest;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/onboarding")
public class OnTrackOnboardingResource {

	@POST
	@Path("/user")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String onboarding(final OnboardingApiRequest request, @Context final HttpServletRequest req) {
		final StringBuffer url = req.getRequestURL();
		final String baseURL = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/";

		final BusinessLogic businessLogic = ServerServiceProvider.getInstance().getBusinessLogic();
		final User user = businessLogic.createTrialUser(request.getEmail(), Profile.ACCOUNT_MANAGER);
		businessLogic.sendOnboardingMail(user.getEmail(), user);

		final String accessToken = user.getId().toString();

		final String onboard = baseURL + "onboarding/access/" + accessToken;
		return onboard;
	}
}
