package edu.kit.ifv.mobitopp.visum;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.Serializable;


public class VisumEdge 
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final Integer id;

	public final VisumPoint from;
	public final VisumPoint to;

	public final List<VisumPoint> intermediate;


	public VisumEdge(Integer id, VisumPoint from, VisumPoint to,  List<VisumPoint> intermediate) {

		this.id = id;
		this.from = from;
		this.to = to;

		this.intermediate =  Collections.unmodifiableList(intermediate);
	}

	public VisumEdge(VisumPoint from, VisumPoint to) {
		this(null, from, to, new ArrayList<VisumPoint>(0));
	}

	public String toString() {

		return "(" + from + "->" + to + ":\n\t" + intermediateToString() + ")";

	}

	protected String intermediateToString() {

		String s = "";

		for (VisumPoint p : intermediate) {
			s += p + ",";
		}

		return s;
	}
}
