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

	private ModelStateManager<AnnotationType> stateManager;

	private ModelStateManager<DeprecationState> deprecationManager;

	public Annotation() {}

	public Annotation(final UUID id, final User author, final Date date, final String message) {
		this.id = id;
		this.message = message;
		this.stateManager = new ModelStateManager<AnnotationType>(AnnotationType.SIMPLE, author, date);
		this.deprecationManager = new ModelStateManager<DeprecationState>(DeprecationState.VALID, author, date);
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
		return stateManager.getInitialState().getAuthor();
	}

	public String getMessage() {
		return message == null ? "" : message;
	}

	public UUID getId() {
		return id;
	}

	public Date getCreationDate() {
		return stateManager.getInitialState().getTimestamp();
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

	public boolean isDeprecated() {
		return deprecationManager.getCurrentStateValue() == DeprecationState.DEPRECATED;
	}

	public Date getDeprecationTimestamp(final DeprecationState state) {
		final ModelState<DeprecationState> lastOccurenceOf = deprecationManager.getLastOccurenceOf(state);
		return lastOccurenceOf == null ? null : lastOccurenceOf.getTimestamp();
	}

	public User getDeprecationAuthor(final DeprecationState state) {
		final ModelState<DeprecationState> lastOccurenceOf = deprecationManager.getLastOccurenceOf(state);
		return lastOccurenceOf == null ? null : deprecationManager.getLastOccurenceOf(state).getAuthor();
	}

	public Date getTimestampForState(final AnnotationType state) {
		final ModelState<AnnotationType> lastOccurenceOf = stateManager.getLastOccurenceOf(state);
		return lastOccurenceOf == null ? null : lastOccurenceOf.getTimestamp();
	}

	public User getAuthorForState(final AnnotationType state) {
		final ModelState<AnnotationType> lastOccurenceOf = stateManager.getLastOccurenceOf(state);
		return lastOccurenceOf == null ? null : stateManager.getLastOccurenceOf(state).getAuthor();
	}

	public AnnotationType getType() {
		return stateManager.getCurrentStateValue();
	}

	public void setType(final AnnotationType newState, final User author, final Date timestamp) {
		stateManager.setState(newState, author, timestamp);
	}

	public void setDeprecation(final DeprecationState newState, final User author, final Date timestamp) {
		deprecationManager.setState(newState, author, timestamp);
	}

	public boolean isImpeded() {
		return stateManager.getCurrentStateValue() == AnnotationType.OPEN_IMPEDIMENT;
	}

}
