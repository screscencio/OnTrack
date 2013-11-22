package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.file.FileUploadActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(FileUploadActionEntity.class)
public class FileUploadAction implements FileAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID fileRepresentationId;

	@Attribute
	private String fileName;

	@Attribute
	private String filePath;

	@Element
	private UUID uniqueId;

	@Override
	public UUID getId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	protected FileUploadAction() {}

	public FileUploadAction(final FileRepresentation fileRepresentation) {
		this(fileRepresentation.getId(), fileRepresentation.getFileName(), fileRepresentation.getFilePath());
	}

	public FileUploadAction(final UUID fileRepresentationId, final String fileName, final String filePath) {
		this.uniqueId = new UUID();
		this.fileName = fileName;
		this.fileRepresentationId = fileRepresentationId;
		this.filePath = filePath;

	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		context.addFileRepresentation(new FileRepresentation(fileRepresentationId, fileName, filePath, context.getProjectRepresentation().getId()));
		return null;
	}

	@Override
	public UUID getReferenceId() {
		return fileRepresentationId;
	}

	public String getFileName() {
		return fileName;
	}

}
