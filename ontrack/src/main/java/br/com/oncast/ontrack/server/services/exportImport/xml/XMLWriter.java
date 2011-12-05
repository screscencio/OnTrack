package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.OutputStream;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackXML;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectAuthorizationXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;

public class XMLWriter {

	private final OntrackXML ontrackXML;

	public XMLWriter() {
		ontrackXML = new OntrackXML();
	}

	public XMLWriter setUserList(final List<UserXMLNode> userList) {
		ontrackXML.setUsers(userList);
		return this;
	}

	public XMLWriter setVersion(final String version) {
		ontrackXML.setVersion(version);
		return this;
	}

	public XMLWriter setProjectList(final List<ProjectXMLNode> projectList) {
		ontrackXML.setProjects(projectList);
		return this;
	}

	public XMLWriter setProjectAuthorizationList(final List<ProjectAuthorizationXMLNode> projectAuthorizations) {
		ontrackXML.setProjectAuthorizations(projectAuthorizations);
		return this;
	}

	public void export(final OutputStream outputStream) {
		final Serializer serializer = new Persister();

		try {
			serializer.write(ontrackXML, outputStream);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
