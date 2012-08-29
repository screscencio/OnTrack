package br.com.oncast.ontrack.shared.model.annotation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.ModelState;
import br.com.oncast.ontrack.shared.model.ModelStateManager;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Annotation implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private String message;

	private FileRepresentation attachmentFile;

	private Set<User> voters;

	private ModelStateManager<Boolean> deprecationState;

	public Annotation() {}

	public Annotation(final UUID id, final User author, final Date date, final String message) {
		this.id = id;
		this.message = message;
		this.deprecationState = new ModelStateManager<Boolean>(false, author, date);
		voters = new HashSet<User>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Annotation other = (Annotation) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	public User getAuthor() {
		return deprecationState.getInitialState().getAuthor();
	}

	public String getMessage() {
		return message == null ? "" : message;
	}

	public UUID getId() {
		return id;
	}

	public Date getCreationDate() {
		return deprecationState.getInitialState().getTimestamp();
	}

	public int getVoteCount() {
		return voters.size();
	}

	public void vote(final User voter) {
		voters.add(voter);
	}

	public void removeVote(final User user) {
		voters.remove(user);
	}

	public boolean hasVoted(final User user) {
		return voters.contains(user);
	}

	public FileRepresentation getAttachmentFile() {
		return attachmentFile;
	}

	public void setAttachmentFile(final FileRepresentation attachmentFile) {
		this.attachmentFile = attachmentFile;
	}

	public void setDeprecated(final boolean deprecated, final User deprecationAuthor, final Date deprecationTimestamp) {
		deprecationState.setState(deprecated, deprecationAuthor, deprecationTimestamp);
	}

	public boolean isDeprecated() {
		return deprecationState.getCurrentStateValue();
	}

	public Date getDeprecationTimestamp() {
		return getDeprecationTimestampForState(true);
	}

	public User getDeprecationAuthor() {
		return getDeprecationAuthorForState(true);
	}

	public Date getDeprecationRemovalTimestamp() {
		return getDeprecationTimestampForState(false);
	}

	public User getDeprecationRemovalAuthor() {
		return getDeprecationAuthorForState(false);
	}

	private Date getDeprecationTimestampForState(final boolean b) {
		final ModelState<Boolean> lastOccurenceOf = deprecationState.getLastOccurenceOf(b);
		return lastOccurenceOf == null ? null : lastOccurenceOf.getTimestamp();
	}

	private User getDeprecationAuthorForState(final boolean b) {
		final ModelState<Boolean> lastOccurenceOf = deprecationState.getLastOccurenceOf(b);
		return lastOccurenceOf == null ? null : deprecationState.getLastOccurenceOf(b).getAuthor();
	}

}
