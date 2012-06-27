package br.com.oncast.ontrack.shared.model.annotation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Annotation implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;
	private User author;
	private String message;
	private FileRepresentation attachmentFile;
	private Date date;

	private Set<String> voters;

	public Annotation() {}

	public Annotation(final UUID id, final User author, final Date date, final String message) {
		this.id = id;
		this.author = author;
		this.date = date;
		this.message = message;
		voters = new HashSet<String>();
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
		return author;
	}

	public String getMessage() {
		return message;
	}

	public UUID getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public int getVoteCount() {
		return voters.size();
	}

	public void vote(final String voterEmail) {
		voters.add(voterEmail);
	}

	public void removeVote(final String voterEmail) {
		voters.remove(voterEmail);
	}

	public boolean hasVoted(final String voterEmail) {
		return voters.contains(voterEmail);
	}

	public FileRepresentation getAttachmentFile() {
		return attachmentFile;
	}

	public void setAttachmentFile(final FileRepresentation attachmentFile) {
		this.attachmentFile = attachmentFile;
	}

}
