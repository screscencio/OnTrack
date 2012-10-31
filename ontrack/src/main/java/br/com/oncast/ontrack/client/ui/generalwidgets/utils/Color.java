package br.com.oncast.ontrack.client.ui.generalwidgets.utils;

public class Color {

	private static final double DEFAULT_ALPHA = 1.0;

	public static Color TRANSPARENT = new Color(255, 255, 255, 0);
	public static Color GREEN = new Color(212, 250, 22);
	public static Color YELLOW = new Color(250, 231, 22);
	public static Color BLUE = new Color(154, 203, 230);
	public static Color RED = new Color(240, 84, 45);

	private int r;
	private int g;
	private int b;
	private double a = -DEFAULT_ALPHA;

	public Color(final String hexColor) {
		if (hexColor.length() != 7 || hexColor.indexOf("#") != 0) throw new IllegalArgumentException("hexColor should be CCS Color style (Eg. #aabbcc)");

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
}
