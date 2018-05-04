package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.nodes.Tag;

import edu.kit.ifv.mobitopp.data.local.LocalFiles;

public class ParserBuilder {

	private final Map<Class<?>, Tag> tags;

	public ParserBuilder() {
		tags = new HashMap<>(defaultTags());
	}

	public void addTag(Class<?> type, String tag) {
		tags.put(type, new Tag(tag));
	}

	public PopulationSynthesisParser forPopulationSynthesis() {
		return new PopulationSynthesisParser(tags);
	}

	public SimulationParser forSimulation() {
		return new SimulationParser(tags);
	}

	private static Map<Class<?>, Tag> defaultTags() {
		Map<Class<?>, Tag> mapping = new HashMap<>();
		mapping.put(LocalFiles.class, new Tag("!file"));
		mapping.put(UsePublicTransport.class, new Tag("!usePublicTransport"));
		mapping.put(UseZoneProfiles.class, new Tag("!useZoneProfiles"));
		mapping.put(NoPublicTransport.class, new Tag("!deactivated"));
		mapping.put(ConnectionScanAlgorithm.class, new Tag("!connectionScan"));
		mapping.put(ProfileScanAlgorithm.class, new Tag("!profileScan"));
		return mapping;
	}

}
