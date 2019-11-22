package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class CommunityRelationsParser {

	private final Map<String, Community> communities;
	private final Map<Community, Map<Community, Integer>> relations;

	public CommunityRelationsParser(Map<String, Community> communities) {
		super();
		this.communities = communities;
		this.relations = new LinkedHashMap<>();
	}

	public DefaultCommunityRepository parse(File input) {
		load(input).forEach(this::parse);
		return new DefaultCommunityRepository(Collections.unmodifiableMap(relations));
	}

	Stream<Row> load(File input) {
		return CsvFile.createFrom(input).stream();
	}
	
	private void parse(Row row) {
		Community origin = communities.get(row.get("origin"));
		Community destination = communities.get(row.get("destination"));
		int commuters = row.valueAsInteger("commuters");
		getMapping(origin).put(destination, commuters);
	}

	private Map<Community, Integer> getMapping(Community origin) {
		if (!relations.containsKey(origin)) {
			relations.put(origin, new LinkedHashMap<>());
		}
		return relations.get(origin);
	}

}
