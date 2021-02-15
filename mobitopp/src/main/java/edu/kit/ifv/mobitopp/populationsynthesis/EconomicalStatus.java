package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Slf4j
public enum EconomicalStatus {

	veryLow(1),
	low(2),
	middle(3),
	high(4),
	veryHigh(5);

	private final int code;

	public static EconomicalStatus of(int code) {
		return Stream
				.of(values())
				.filter(s -> s.code == code)
				.findAny()
				.orElseThrow(() -> warn(new IllegalArgumentException(
						String.format("No economical status for code %s available.", code)), log));
	}

}
