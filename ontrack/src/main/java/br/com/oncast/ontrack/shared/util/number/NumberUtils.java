package br.com.oncast.ontrack.shared.util.number;

public class NumberUtils {

	// TODO Review this number formating method;
	public static native String roundFloat(final float number, int decimalDigits) /*-{
		var num = new Number(number);
		return num.toFixed(decimalDigits).toString();
	}-*/;

}