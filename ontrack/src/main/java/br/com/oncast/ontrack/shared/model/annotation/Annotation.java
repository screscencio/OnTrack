package br.com.oncast.ontrack.shared.model.annotation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.ModelState;
import br.com.oncast.ontrack.shared.model.ModelStateManager;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Annotation implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private String message;

	private FileRepresentation attachmentFile;

	private Set<UserRepresentation> voters;

	private ModelStateManager<AnnotationType> stateManager;

	private ModelStateManager<DeprecationState> deprecationManager;

	public Annotation() {}

	public Annotation(final UUID id, final UserRepresentation author, final Date date, final String message, final AnnotationType type) {
		this.id = id;
		this.message = message;
		this.stateManager = new ModelStateManager<AnnotationType>(type, author, date);
		this.deprecationManager = new ModelStateManager<DeprecationState>(DeprecationState.VALID, author, date);
		voters = new HashSet<UserRepresentation>();
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

	public UserRepresentation getAuthor() {
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

	public void vote(final UserRepresentation voter) {
		voters.add(voter);
	}

	public void removeVote(final UserRepresentation user) {
		voters.remove(user);
	}

	public boolean hasVoted(final UserRepresentation user) {
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

	public UserRepresentation getDeprecationAuthor(final DeprecationState state) {
		final ModelState<DeprecationState> lastOccurenceOf = deprecationManager.getLastOccurenceOf(state);
		return lastOccurenceOf == null ? null : deprecationManager.getLastOccurenceOf(state).getAuthor();
	}

	public Date getLastOcuurenceOf(final AnnotationType state) {
		final ModelState<AnnotationType> lastOccurenceOf = stateManager.getLastOccurenceOf(state);
		return lastOccurenceOf == null ? null : lastOccurenceOf.getTimestamp();
	}

	public UserRepresentation getAuthorForState(final AnnotationType state) {
		final ModelState<AnnotationType> lastOccurenceOf = stateManager.getLastOccurenceOf(state);
		return lastOccurenceOf == null ? null : stateManager.getLastOccurenceOf(state).getAuthor();
	}

	public AnnotationType getType() {
		return stateManager.getCurrentStateValue();
	}

	public void setType(final AnnotationType newState, final UserRepresentation author, final Date timestamp) {
		stateManager.setState(newState, author, timestamp);
	}

	public void setDeprecation(final DeprecationState newState, final UserRepresentation author, final Date timestamp) {
		deprecationManager.setState(newState, author, timestamp);
	}

	public boolean isImpeded() {
		return stateManager.getCurrentStateValue() == AnnotationType.OPEN_IMPEDIMENT;
	}

	public long getDurationOf(final AnnotationType state) {
		return stateManager.getDurationOfState(state);
	}

	public long getCurrentStateDuration() {
		return stateManager.getCurrentStateDuration();
	}

}
