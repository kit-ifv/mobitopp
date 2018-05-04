package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;


public class VisumLink
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final int id;

	public final VisumOrientedLink linkA;
	public final VisumOrientedLink linkB;


	public VisumLink(
		int id,
		VisumOrientedLink linkA,
		VisumOrientedLink linkB
	) {

		this.id = id;
		this.linkA = linkA;
		this.linkB = linkB;
	}


	public String toString() {
		return "VisumLink(" + id + ",\n\t" + linkA.toString() + ",\n\t" + linkB.toString() + "\n)";
	}




}
