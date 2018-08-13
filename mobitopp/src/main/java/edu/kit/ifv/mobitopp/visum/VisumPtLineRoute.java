package edu.kit.ifv.mobitopp.visum;

import java.util.SortedMap;
import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class VisumPtLineRoute 
	implements Serializable
{


	public final VisumPtLine line;
	public final String name;
	public final VisumPtLineRouteDirection direction;


	public SortedMap<Integer,VisumPtLineRouteElement> elements;

	public VisumPtLineRoute(
		VisumPtLine line,
		String name,
		VisumPtLineRouteDirection direction
	) {

		this.line = line;
		this.name = name;
		this.direction = direction;
	}

	public String toString() {

		return "VisumPtLineRoute("
						+ line + "," 
						+ name + "," 
						+ direction + ")";
	}


	public List<VisumNode> routeAsNodes() {

		List<VisumNode> nodes = new ArrayList<VisumNode>();

		for (VisumPtLineRouteElement element : this.elements.values()) {

			if (element.node != null) {
				nodes.add(element.node);
			} else {
				nodes.add(element.stopPoint.stopArea.node);
			}
		}

		return nodes;
	}



}
