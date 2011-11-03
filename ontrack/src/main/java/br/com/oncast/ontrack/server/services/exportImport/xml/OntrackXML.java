package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import br.com.oncast.ontrack.server.business.UserAction;
import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.shared.model.user.User;

@Root
public class OntrackXML {

	@Attribute
	private long version;

	@ElementList
	private List<User> users;

	@ElementList
	private List<Password> passwords;

	@ElementList
	private List<UserAction> userActions;

	public void setUsers(final List<User> users) {
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<Password> getPasswords() {
		return passwords;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(final long version) {
		this.version = version;
	}

	public void setPasswords(final List<Password> passwords) {
		this.passwords = passwords;
	}

	public void setUserActions(final List<UserAction> userActions) {
		this.userActions = userActions;
	}

	public List<UserAction> getUserActions() {
		return userActions;
	}
}
