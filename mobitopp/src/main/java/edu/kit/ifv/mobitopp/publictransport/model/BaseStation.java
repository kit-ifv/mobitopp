package edu.kit.ifv.mobitopp.publictransport.model;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.network.Node;

public abstract class BaseStation implements Station {

	private final int id;
	private final Set<Stop> stops;
	private final List<Node> nodes;

	public BaseStation(int id, Collection<Node> nodes) {
		super();
		this.id = id;
		stops = new TreeSet<>(comparing(Stop::id));
		this.nodes = new ArrayList<>(nodes);
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public void add(Stop newStop) {
		stops.add(newStop);
	}

	@Override
	public void forEach(Consumer<Stop> action) {
		stops.forEach(action);
	}

	@Override
	public void forEachNode(Consumer<Node> consumer) {
		for (Node node : nodes) {
			consumer.accept(node);
		}
	}

}