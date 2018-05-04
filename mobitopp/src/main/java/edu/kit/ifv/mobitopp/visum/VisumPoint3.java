package edu.kit.ifv.mobitopp.visum;

public class VisumPoint3 
	extends VisumPoint2 {

	private static final long serialVersionUID = 1L;

	public final float z;

	public VisumPoint3(float x, float y, float z) {
		super(x, y);
		this.z=z;
	}

	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}
}
