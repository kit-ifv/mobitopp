package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.StringTokenizer;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.DefaultStation;
import edu.kit.ifv.mobitopp.publictransport.model.Station;

public class CsvStationFormat extends CsvFormat implements StationFormat {

	private static final int nodesIndex = 1;

	static final String nodeSeparator = ",";

	private static final int idIndex = 0;

	@Override
	public Station deserialize(String serializedStation, NodeResolver nodeResolver) {
		String[] fields = serializedStation.split(separator);
		int id = idOf(fields);
		List<Node> nodes = nodesOf(fields, nodeResolver);
		return new DefaultStation(id, nodes);
	}

	private List<Node> nodesOf(String[] fields, NodeResolver nodeResolver) {
		String serialized = fields[nodesIndex];
		StringTokenizer tokenizer = new StringTokenizer(serialized, nodeSeparator);
		List<Node> nodes = new ArrayList<>();
		while(tokenizer.hasMoreTokens()) {
			int id = Integer.parseInt(tokenizer.nextToken());
			nodes.add(nodeResolver.resolve(id));
		}
		return nodes;
	}

	private int idOf(String[] fields) {
		return Integer.parseInt(fields[idIndex]);
	}

	@Override
	public String serialize(Station station) {
		StringBuilder serialized = new StringBuilder();
		serialized.append(station.id());
		serialized.append(separator);
		serialized.append(serializedNodesOf(station));
		return serialized.toString();
	}

	private static String serializedNodesOf(Station station) {
		StringJoiner serializedNodes = new StringJoiner(nodeSeparator);
		station.forEachNode(node -> serializedNodes.add(asString(node.id())));
		return serializedNodes.toString();
	}

	private static String asString(int id) {
		return String.valueOf(id);
	}

}
