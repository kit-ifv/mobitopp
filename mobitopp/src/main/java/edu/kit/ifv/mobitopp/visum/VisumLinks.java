package edu.kit.ifv.mobitopp.visum;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class VisumLinks
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public Map<Integer, VisumLink> links;


	private static Map<VisumNode,Map<VisumNode,VisumOrientedLink>> mappedLinks;



	public VisumLinks(Map<Integer, VisumLink> links) {

		this.links = Collections.unmodifiableMap(links);

		initMappedLinks(links);
	}


	private static void initMappedLinks(Map<Integer, VisumLink> links) {

		mappedLinks = new HashMap<VisumNode,Map<VisumNode,VisumOrientedLink>>();

		for (VisumLink link : links.values()) {

			if (!mappedLinks.containsKey(link.linkA.from)) {
					mappedLinks.put(link.linkA.from, new HashMap<VisumNode,VisumOrientedLink>());
			}

			mappedLinks.get(link.linkA.from).put(link.linkA.to, link.linkA);

			if (!mappedLinks.containsKey(link.linkB.from)) {
					mappedLinks.put(link.linkB.from, new HashMap<VisumNode,VisumOrientedLink>());
			}

			mappedLinks.get(link.linkB.from).put(link.linkB.to, link.linkB);

		}
	}


	public VisumOrientedLink getOrientedLink(VisumNode from, VisumNode to) {

		return mappedLinks.get(from).get(to);	
	}


}
