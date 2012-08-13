package br.com.oncast.ontrack.shared.model.annotation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.annotation.AnnotationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(AnnotationEntity.class)
public class Annotation implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private User author;

	private String message;

	private FileRepresentation attachmentFile;

	private Date creationDate;

	private Set<User> voters;

	private boolean deprecated;

	public Annotation() {}

	public Annotation(final UUID id, final User author, final Date date, final String message) {
		this.id = id;
		this.author = author;
		this.creationDate = date;
		this.message = message;
		voters = new HashSet<User>();
		deprecated = false;
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

	public Date getCreationDate() {
		return creationDate;
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

	public void setDeprecated(final boolean deprecated) {
		this.deprecated = deprecated;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

}
