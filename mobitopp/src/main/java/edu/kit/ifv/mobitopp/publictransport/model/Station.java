package edu.kit.ifv.mobitopp.publictransport.model;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public interface Station {

	int id();
	
	RelativeTime minimumChangeTime(int id);

	void add(Stop newStop);

	void forEach(Consumer<Stop> action);

	void forEachNode(Consumer<Node> consumer);
	
}