package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum EconomicalStatus {

	veryLow(1),
	low(1),
	middle(1),
	high(1),
	veryHigh(1);

	private final int code;

	public static EconomicalStatus of(int code) {
		return Stream
				.of(values())
				.filter(s -> s.code == code)
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("No economical status for code %s available.", code)));
	}

}
