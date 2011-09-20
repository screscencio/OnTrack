package br.com.oncast.ontrack;

import static br.com.oncast.ontrack.utils.Config.ONTRACK_INSTANCES;
import static br.com.oncast.ontrack.utils.Config.ORIGINAL_PERSISTENCE_XML;
import static br.com.oncast.ontrack.utils.Config.ORIGINAL_WAR_PACKAGE;

import java.io.File;

import br.com.oncast.ontrack.packaging.OntrackPersistenceXmlRenderer;
import br.com.oncast.ontrack.packaging.OntrackWarPackage;
import br.com.oncast.ontrack.utils.Config;
import br.com.oncast.ontrack.utils.StringUtils;
import br.com.oncast.ontrack.utils.Workpaths;

public class ReleasePackageGenerator {
	public static void main(final String[] args) {
		System.out.println("Generating war the following instances: " + StringUtils.join(ONTRACK_INSTANCES.splitValue(), ", "));

		final OntrackWarPackage war = OntrackWarPackage.prepare(new File(ORIGINAL_WAR_PACKAGE.value()));
		try {
			final OntrackPersistenceXmlRenderer persistenceXml = OntrackPersistenceXmlRenderer.prepare(new File(ORIGINAL_PERSISTENCE_XML.value()));
			for (final String instance : Config.ONTRACK_INSTANCES.splitValue()) {
				System.out.println("¥ Generanting war for instance '" + instance + "'.");
				war.setPersistenceXml(persistenceXml.render(instance));
				war.output(new File(Workpaths.wars(), instance + ".war"));
			}
		}
		finally {
			war.cleanup();
		}
	}
}
