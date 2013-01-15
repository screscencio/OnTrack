package br.com.oncast.ontrack.shared.model.color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColorPack implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final List<ColorPack> PACKS = new ArrayList<ColorPack>();

	static {
		PACKS.add(new ColorPack(new Color(70, 70, 70), new Color(231, 231, 231)));
		PACKS.add(new ColorPack(new Color(13, 52, 114), new Color(182, 207, 245)));
		PACKS.add(new ColorPack(new Color(13, 59, 68), new Color(152, 215, 228)));
		PACKS.add(new ColorPack(new Color(61, 24, 142), new Color(227, 215, 255)));
		PACKS.add(new ColorPack(new Color(113, 26, 54), new Color(251, 211, 224)));
		PACKS.add(new ColorPack(new Color(138, 28, 10), new Color(242, 178, 168)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(194, 194, 194)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(73, 134, 231)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(45, 162, 187)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(185, 174, 255)));
		PACKS.add(new ColorPack(new Color(153, 74, 100), new Color(246, 145, 178)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(251, 76, 47)));
		PACKS.add(new ColorPack(new Color(122, 46, 11), new Color(255, 200, 175)));
		PACKS.add(new ColorPack(new Color(122, 71, 6), new Color(255, 222, 181)));
		PACKS.add(new ColorPack(new Color(89, 76, 5), new Color(251, 233, 131)));
		PACKS.add(new ColorPack(new Color(104, 78, 7), new Color(253, 237, 193)));
		PACKS.add(new ColorPack(new Color(11, 79, 48), new Color(179, 239, 211)));
		PACKS.add(new ColorPack(new Color(4, 80, 46), new Color(162, 220, 192)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(255, 117, 55)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(255, 173, 70)));
		PACKS.add(new ColorPack(new Color(102, 46, 55), new Color(235, 219, 222)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(204, 166, 172)));
		PACKS.add(new ColorPack(new Color(9, 66, 40), new Color(66, 214, 146)));
		PACKS.add(new ColorPack(new Color(255, 255, 255), new Color(22, 167, 101)));
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

	@Override
	public String toString() {
		return "Fg: " + foreground.toCssRepresentation() + " \t Bg: " + background.toCssRepresentation();
	}
}
