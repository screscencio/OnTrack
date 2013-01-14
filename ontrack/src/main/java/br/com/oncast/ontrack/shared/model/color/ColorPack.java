package br.com.oncast.ontrack.shared.model.color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColorPack implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final List<ColorPack> PACKS = new ArrayList<ColorPack>();

	static {
		PACKS.add(new ColorPack(new Color(231, 231, 231), new Color(70, 70, 70)));
		PACKS.add(new ColorPack(new Color(182, 207, 245), new Color(13, 52, 114)));
		PACKS.add(new ColorPack(new Color(152, 215, 228), new Color(13, 59, 68)));
		PACKS.add(new ColorPack(new Color(227, 215, 255), new Color(61, 24, 142)));
		PACKS.add(new ColorPack(new Color(251, 211, 224), new Color(113, 26, 54)));
		PACKS.add(new ColorPack(new Color(242, 178, 168), new Color(138, 28, 10)));
		PACKS.add(new ColorPack(new Color(194, 194, 194), new Color(255, 255, 255)));
		PACKS.add(new ColorPack(new Color(73, 134, 231), new Color(255, 255, 255)));
		PACKS.add(new ColorPack(new Color(45, 162, 187), new Color(255, 255, 255)));
		PACKS.add(new ColorPack(new Color(185, 154, 255), new Color(255, 255, 255)));
		PACKS.add(new ColorPack(new Color(246, 145, 178), new Color(153, 74, 100)));
		PACKS.add(new ColorPack(new Color(251, 76, 47), new Color(255, 255, 255)));
	}

	private Color background;
	private Color foreground;

	public static List<ColorPack> getDefaultColorPacks() {
		return PACKS;
	}

	public ColorPack() {}

	public ColorPack(final Color foreground, final Color background) {
		this.foreground = foreground;
		this.background = background;
	}

	public Color getBackground() {
		return background;
	}

	public Color getForeground() {
		return foreground;
	}
}
