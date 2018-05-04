package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.routing.Link;
import edu.kit.ifv.mobitopp.routing.LinkFromVisumLink;
import edu.kit.ifv.mobitopp.routing.NodeFromVisumNode;
import edu.kit.ifv.mobitopp.routing.VisumLinkFactory;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;

class VisumWalkLinkFactory implements VisumLinkFactory {

	@Override
	public Link create(VisumOrientedLink link, NodeFromVisumNode from, NodeFromVisumNode to) {
		float travelTime = 60*(link.length / maxSpeed(link));
		return new LinkFromVisumLink(link, from, to, link.length, travelTime);
	}
	
	private int maxSpeed(VisumOrientedLink link) {
		return link.attributes.walkSpeed != 0 ? link.attributes.walkSpeed
				: link.linkType.attributes.walkSpeed;
	}

}
