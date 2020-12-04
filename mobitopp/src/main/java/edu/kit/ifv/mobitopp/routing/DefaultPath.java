package edu.kit.ifv.mobitopp.routing;

import java.util.ArrayList;
import java.util.List;

public class DefaultPath 
	implements Path 
{

	final List<Link> elements = new ArrayList<Link>();

	private final boolean valid;

	private final float travelTime;


	public static final Path emptyPath = new DefaultPath(true);
	public static final Path invalidPath = new DefaultPath(false);


	private DefaultPath(boolean valid) {
		this.valid = valid;
		travelTime = 0.0f;
	}

	public DefaultPath(List<Link> links, float travelTime) {

		for (Link link : links) {
			add(link);
		}

		this.travelTime = travelTime;
		this.valid = true;
	}

	public float travelTime() {
		return this.travelTime;
	}

	public Node from() {

		return elements.get(0).from();
	}

	public Node to() {

		if (elements.isEmpty()) { return null; }

		return elements.get(elements.size()-1).to();
	}

	public Link first() {

		return elements.get(0);
	}

	public Link next(Link link) {

		assert elements.contains(link);

		int idx = elements.indexOf(link);
		return elements.get(idx+1);
	}

	public Link get(int idx) {

		return elements.get(idx);
	}

	public int size() {

		if (valid) {
			return elements.size();
		} else {
			return -1;
		}
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public boolean isValid() {
		return valid;
	}

	public Float length() {
		Float len = 0.0f;

		for (Link link : elements) {
			len += link.distance();
		}

		if (valid) {
			return len;
		} else {
			return Float.POSITIVE_INFINITY;
		}
			
	}

	public String toString() {

		String s = "<";

		for (Link link : this.elements) {

			s += link.toString();
		}

		s += ">";

		return s;
	}


	private void add(Link link) {
		assert link != null;
		assert to() == null || to() == link.from();

		this.elements.add(link);
	}


	public static Path makePath(List<Link> links) {
		assert links != null;
		return new DefaultPath(links, 0.0f);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + Float.floatToIntBits(travelTime);
		result = prime * result + (valid ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultPath other = (DefaultPath) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (Float.floatToIntBits(travelTime) != Float.floatToIntBits(other.travelTime))
			return false;
		if (valid != other.valid)
			return false;
		return true;
	}
	
}
