package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.network.Node;

public interface NodeResolver {

	Node resolve(int nodeId);

}
