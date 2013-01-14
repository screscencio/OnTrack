package br.com.oncast.ontrack.shared.model.color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;

public class Color implements Serializable {

	private static final String HASH = "#";

	private static final long serialVersionUID = 1L;

	private static final List<Color> PRESET_COLORS = new ArrayList<Color>();

	private static final double DEFAULT_ALPHA = 1.0;

	public static final Color TRANSPARENT = new Color(255, 255, 255, 0);
	public static final Color GREEN = new Color(212, 250, 22);
	public static final Color YELLOW = new Color(250, 231, 22);
	public static final Color BLUE = new Color(154, 203, 230);
	public static final Color GRAY = new Color(200, 200, 180);
	public static final Color RED = new Color(240, 84, 45);

	static {
		PRESET_COLORS.add(new Color("#FF4D50"));
		PRESET_COLORS.add(new Color("#00DD00"));
		PRESET_COLORS.add(new Color("#9970FF"));
		PRESET_COLORS.add(new Color("#FFAACC"));
		PRESET_COLORS.add(new Color("#EE7F2B"));
		PRESET_COLORS.add(new Color("#07D2FF"));
		PRESET_COLORS.add(new Color("#227FFF"));
		PRESET_COLORS.add(new Color("#AAFA55"));
		PRESET_COLORS.add(new Color("#FFC14B"));
		PRESET_COLORS.add(new Color("#A8C102"));
		PRESET_COLORS.add(new Color("#9C7835"));
		PRESET_COLORS.add(new Color("#BB8F73"));
		PRESET_COLORS.add(new Color("#AACCFF"));
		PRESET_COLORS.add(new Color("#CCF56F"));
		PRESET_COLORS.add(new Color("#CC6FF5"));
		PRESET_COLORS.add(new Color("#FA55AA"));
	}

	@Attribute
	private int r;

	@Attribute
	private int g;

	@Attribute
	private int b;

	@Attribute
	private double a = -DEFAULT_ALPHA;

	protected Color() {}

	public Color(final String hexColor) {
		if (hexColor.length() != 7 || hexColor.indexOf(HASH) != 0) throw new IllegalArgumentException("hexColor should be CCS Color style (Eg. #aabbcc)");

		r = Integer.parseInt(hexColor.substring(1, 3), 16);
		g = Integer.parseInt(hexColor.substring(3, 5), 16);
		b = Integer.parseInt(hexColor.substring(5, 7), 16);

		a = DEFAULT_ALPHA;
	}

	public Color(final int r, final int g, final int b) {
		this(r, g, b, DEFAULT_ALPHA);
	}

	public Color(final int r, final int g, final int b, final double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public String toHex() {
		String hex = HASH;
		hex += toTwoDigitHex(r);
		hex += toTwoDigitHex(g);
		hex += toTwoDigitHex(b);
		return hex;
	}

	private String toTwoDigitHex(final int x) {
		final String hexString = Integer.toHexString(x);
		return hexString.length() <= 1 ? "0" + hexString : hexString;
	}

	public int getRed() {
		return r;
	}

	public int getGreen() {
		return g;
	}

	public int getBlue() {
		return b;
	}

	public double getAlpha() {
		return a;
	}

	public Color setAlpha(final double a) {
		this.a = a;
		return this;
	}

	public Color setRed(final int r) {
		this.r = r;
		return this;
	}

	public Color setGreen(final int g) {
		this.g = g;
		return this;
	}

	public Color setBlue(final int b) {
		this.b = b;
		return this;
	}

	public String toCssRepresentation() {
		if (a < 0) return "rgb(" + r + ", " + g + ", " + b + ")";
		if (a == 0) return "transparent";
		return "rgba(" + r + ", " + g + ", " + b + ", " + a + ")";
	}

	public Color copy() {
		return new Color(r, g, b, a);
	}

	public static List<Color> getPresetColors() {
		return PRESET_COLORS;
	}

}
