package edu.kit.ifv.mobitopp.visum;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.awt.geom.Area;
import java.awt.geom.Point2D;

import java.io.Serializable;
import java.io.ObjectStreamException;

@SuppressWarnings("serial")
public class VisumSurface 
	implements Serializable
{

	public final Integer id;

	private final List<VisumFace> faces;
	private final List<Integer> enclave;

	private List<Area> areas;
	private Area totalArea;

	public VisumSurface(Integer id, List<VisumFace> faces,  List<Integer> enclave) {

		assert faces.size() == enclave.size();

		this.id = id;
		this.faces = Collections.unmodifiableList(faces);
		this.enclave = Collections.unmodifiableList(enclave);
	}

	public VisumSurface(List<VisumFace> faces,  List<Integer> enclave) {
		this(null, faces, enclave);
	}


	protected List<Area> areasFromFaces(List<VisumFace> faces,  List<Integer> enclave) {

		List<Area> positive = new ArrayList<Area>();
		List<Area> negative = new ArrayList<Area>();

		assert faces.size() == enclave.size();

		for (int i=0; i< faces.size(); i++) {

			VisumFace f = faces.get(i);
			Integer enc = enclave.get(i);

			Area area = f.asArea();

			if (enc == 0) {
				positive.add(area);
			} else if (enc == 1) {
				negative.add(area);
			} else {
				throw new AssertionError();
			}
		}

		for (Area pos : positive) {
			for (Area neg : negative) {

				if (pos.intersects(neg.getBounds2D())) {
					pos.subtract(neg);
				}
			}
		}

		return positive;
	}


	public List<Area> areas() {

		if (this.areas == null) {
			 this.areas = Collections.unmodifiableList(areasFromFaces(this.faces, this.enclave));
		}

		return this.areas;
	}

	public String toString() {

		String s = "VisumSurface(faces=" + faces.size() + "; ";

		for (int i=0; i< faces.size(); i++) {

			VisumFace f = faces.get(i);
			Integer enc = enclave.get(i);

			s  += f + ", enc=" + enc + "; ";
		}
		s  += ")";

		return s;
	}

	public Area area() {

		if (totalArea == null) {
			totalArea = combinedArea(areas());
		}

		return totalArea;
	}

	private static Area combinedArea(List<Area> areas) {

System.out.println("calculating combinedArea for " + areas.size() + " faces");

		Area area = new Area();

		for (Area a : areas) {
			area.add(a);
		}

		return area;
	}

	private static Area asArea(List<VisumFace> faces, List<Integer> enclave) {

		Area a = new Area();

		for (int i=0; i<faces.size(); i++) {

			VisumFace f = faces.get(i);

			if (enclave.get(i) == 0) 
			{
				a.add(f.asArea());
			}
			else {
				a.subtract(f.asArea());
			}

		}

		return a;
	}

	public boolean isPointInside(Point2D p) {

		return asArea(this.faces, this.enclave).contains(p);
	}

	public int numFaces() {
		return this.faces.size();
	}

	public VisumFace face(int n) {
		assert n < this.faces.size();

		return this.faces.get(n);
	}

	public boolean enclave(int n) {

		assert n < this.faces.size();
		assert n < this.enclave.size();

		int enc = this.enclave.get(n);

		assert enc == 1 || enc == 0;

		return enc == 1;
	}


	/*
	private Object readResolve() 
		throws ObjectStreamException
	{
		return new VisumSurface(id, faces, enclave);
	}
	*/

}
