package br.com.oncast.ontrack.shared.model.prioritizationCriteria;

import java.util.ArrayList;
import java.util.List;

public class FibonacciScale {

	private static List<String> fibonacciScale;

	public static List<String> getFibonacciScaleList() {
		if (fibonacciScale != null) return fibonacciScale;

		fibonacciScale = new ArrayList<String>();
		fibonacciScale.add("0");
		fibonacciScale.add("1");
		fibonacciScale.add("2");
		fibonacciScale.add("3");
		fibonacciScale.add("5");
		fibonacciScale.add("8");
		fibonacciScale.add("13");
		fibonacciScale.add("21");
		fibonacciScale.add("34");
		fibonacciScale.add("55");
		return fibonacciScale;
	}

}
