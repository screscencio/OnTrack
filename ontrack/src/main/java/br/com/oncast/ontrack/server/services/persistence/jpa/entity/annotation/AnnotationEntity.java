package br.com.oncast.ontrack.server.services.persistence.jpa.entity.annotation;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.file.FileRepresentationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.IgnoreByConversion;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.ListToHashSetConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.user.User;

@Entity
@ConvertTo(Annotation.class)
public class AnnotationEntity {

	@Id
	@GeneratedValue
	@IgnoreByConversion
	private long sequencialDatabaseId;

	@Column(name = "id", nullable = false)
	@ConvertUsing(StringToUuidConverter.class)
	private String id;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "author", nullable = false, updatable = false)
	private User author;

	@Column(name = "message", nullable = true)
	private String message;

	@OneToOne(optional = true)
	@JoinColumn(name = "attachmentFile", nullable = true, updatable = false)
	private FileRepresentationEntity attachmentFile;

	@Column(name = "date", nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date date;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "Annotation_voters")
	@ConvertUsing(ListToHashSetConverter.class)
	private List<User> voters;

	@IgnoreByConversion
	@Column(name = "subjectId", nullable = false)
	private String subjectId;

	@IgnoreByConversion
	@Column(name = "projectId", nullable = false)
	private String projectId;

	public long getSequencialDatabaseId() {
		return sequencialDatabaseId;
	}

	public void setSequencialDatabaseId(final long sequencialDatabaseId) {
		this.sequencialDatabaseId = sequencialDatabaseId;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(final User author) {
		this.author = author;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public FileRepresentationEntity getAttachmentFile() {
		return attachmentFile;
	}

	public void setAttachmentFile(final FileRepresentationEntity attachmentFile) {
		this.attachmentFile = attachmentFile;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public List<User> getVoters() {
		return voters;
	}

	public void setVoters(final List<User> voters) {
		this.voters = voters;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final String subjectId) {
		this.subjectId = subjectId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(final String projectId) {
		this.projectId = projectId;
	}

}
