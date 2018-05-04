package edu.kit.ifv.mobitopp.data;

import java.util.function.BiFunction;

public class SpanOfHours {

	private final int start;
	private final int end;

	public SpanOfHours(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	public <T> T convert(BiFunction<Integer, Integer, T> converter) {
		return converter.apply(start, end);
	}
}
