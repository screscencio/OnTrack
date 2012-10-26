package br.com.oncast.ontrack.client.ui.generalwidgets.utils;

public class Color {

	public static Color GREEN = new Color(212, 250, 22);
	public static Color YELLOW = new Color(250, 231, 22);
	public static Color BLUE = new Color(154, 203, 230);
	public static Color RED = new Color(240, 84, 45);

	private int r;
	private int g;
	private int b;
	private double a = -1.0;

	public Color(final int r, final int g, final int b) {
		this(r, g, b, 1.0);
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

	public void setAlpha(final double a) {
		this.a = a;
	}

	public void setRed(final int r) {
		this.r = r;
	}

	public void setGreen(final int g) {
		this.g = g;
	}

	public void setBlue(final int b) {
		this.b = b;
	}

	public String toCssRepresentation() {
		if (a < 0) return "rgb(" + r + ", " + g + ", " + b + ")";
		return "rgba(" + r + ", " + g + ", " + b + ", " + a + ")";
	}

	public Color copy() {
		return new Color(r, g, b, a);
	}
}
