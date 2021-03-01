package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum RegionalLevel implements Comparable<RegionalLevel> {

	/**
	 * The order of the enum values defines the hierarchy of demand regions from lowest to highest.
	 */
	zone("zone"), district("district"), community("community"), county("county");

	private static final Map<String, RegionalLevel> values = Stream
			.of(RegionalLevel.values())
			.collect(toMap(RegionalLevel::identifier, Function.identity()));
	private final String identifier;

	private RegionalLevel(String identifier) {
		this.identifier = identifier;
	}

	public String identifier() {
		return identifier;
	}
	
	public static RegionalLevel levelOf(String identifier) {
		if (values.containsKey(identifier)) {
			return values.get(identifier);
		}
		throw warn(new IllegalArgumentException(String
				.format("Regional level of identifier '%s' is unknown. Available regional levels are: %s",
						identifier, values.keySet())), log);
	}
}
