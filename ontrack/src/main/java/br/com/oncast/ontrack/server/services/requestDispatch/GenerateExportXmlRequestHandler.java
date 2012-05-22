package br.com.oncast.ontrack.server.services.requestDispatch;

import static br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials.USER_EMAIL;

import java.io.IOException;
import java.io.OutputStream;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.client.services.migration.GenerateExportXmlRequest;
import br.com.oncast.ontrack.client.services.migration.GenerateExportXmlResponse;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.user.User;

public class GenerateExportXmlRequestHandler implements RequestHandler<GenerateExportXmlRequest, GenerateExportXmlResponse> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public GenerateExportXmlResponse handle(final GenerateExportXmlRequest request) throws Exception {
		final User user = SERVICE_PROVIDER.getAuthenticationManager().getAuthenticatedUser();
		if (user != null & !USER_EMAIL.equals(user.getEmail())) throw new RuntimeException(
				"Only admin can get migration xml, please login as admin");

		final StringBuilder builder = new StringBuilder();
		final OutputStream outputStream = new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				builder.append((char) b);
			}
		};
		SERVICE_PROVIDER.getXmlExporterService().export(outputStream);
		return new GenerateExportXmlResponse(builder.toString());
	}
}
