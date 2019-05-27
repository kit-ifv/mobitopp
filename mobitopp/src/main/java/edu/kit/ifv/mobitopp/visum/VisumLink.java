package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Objects;


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
	
	@Override
  public int hashCode() {
    return Objects.hash(id, linkA, linkB);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumLink other = (VisumLink) obj;
    return id == other.id && Objects.equals(linkA, other.linkA)
        && Objects.equals(linkB, other.linkB);
  }

  public String toString() {
		return "VisumLink(" + id + ",\n\t" + linkA.toString() + ",\n\t" + linkB.toString() + "\n)";
	}
}
