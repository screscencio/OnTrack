package br.com.oncast.ontrack.server.services.persistence.jpa.entity.annotation;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "projectId", "subjectId", "id" }))
public class AnnotationEntity {

	@Id
	@Column(name = "id", nullable = false, unique = true, updatable = false)
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

	@Column(name = "creationDate", nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date creationDate;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "Annotation_voters")
	@ConvertUsing(ListToHashSetConverter.class)
	private List<User> voters;

	@Column(name = "deprecated", nullable = false, updatable = true)
	private boolean deprecated;

	@IgnoreByConversion
	@Column(name = "subjectId", nullable = false, updatable = false)
	private String subjectId;

	@IgnoreByConversion
	@Column(name = "projectId", nullable = false, updatable = false)
	private String projectId;

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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date date) {
		this.creationDate = date;
	}

	public List<User> getVoters() {
		return voters;
	}

	public void setVoters(final List<User> voters) {
		this.voters = voters;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(final boolean deprecated) {
		this.deprecated = deprecated;
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
