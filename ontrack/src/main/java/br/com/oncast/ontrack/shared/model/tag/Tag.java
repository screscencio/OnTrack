package br.com.oncast.ontrack.shared.model.tag;

import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;

public class Tag implements HasUUID, Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private String description;

	private ColorPack colorPack;

	protected Tag() {}

	public Tag(final UUID id, final String description, final ColorPack colorPack) {
		this.id = id;
		this.description = description;
		this.setColorPack(colorPack);
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

	public ColorPack getColorPack() {
		return colorPack;
	}

	public void setColorPack(final ColorPack colorPack) {
		this.colorPack = colorPack;
	}
}
