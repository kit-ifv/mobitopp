package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;


@SuppressWarnings("serial")
public class VisumPtLineRoute implements Serializable {

  public final String id;
  public final VisumPtLine line;
  public final String name;
  public final VisumPtLineRouteDirection direction;

	private SortedMap<Integer,VisumPtLineRouteElement> elements;

	public VisumPtLineRoute(
		String id, VisumPtLine line,
		String name,
		VisumPtLineRouteDirection direction
	) {

		this.id = id;
    this.line = line;
		this.name = name;
		this.direction = direction;
	}
	
	public void setElements(SortedMap<Integer, VisumPtLineRouteElement> elements) {
    this.elements = Collections.unmodifiableSortedMap(elements);
	}

	public SortedMap<Integer, VisumPtLineRouteElement> getElements() {
	  return elements;
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
