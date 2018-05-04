package edu.kit.ifv.mobitopp.routing;

import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;

public interface VisumLinkFactory {

	Link create(VisumOrientedLink link, NodeFromVisumNode from, NodeFromVisumNode to);
}
