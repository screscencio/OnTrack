package br.com.oncast.ontrack.shared.model.tag;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

public class Tag implements HasUUID, Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private String description;

	private Color backgroundColor;

	private Color textColor;

	protected Tag() {}

	public Tag(final UUID id, final String description, final Color backgroundColor, final Color textColor) {
		this.id = id;
		this.description = description;
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getTextColor() {
		return textColor;
	}

}
