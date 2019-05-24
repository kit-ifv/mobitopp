package edu.kit.ifv.mobitopp.visum;

import java.util.Objects;

public class VisumPoint3 extends VisumPoint2 {

  private static final long serialVersionUID = 1L;

  public final float z;

  public VisumPoint3(float x, float y, float z) {
    super(x, y);
    this.z = z;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(z);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumPoint3 other = (VisumPoint3) obj;
    return Float.floatToIntBits(z) == Float.floatToIntBits(other.z);
  }

  public String toString() {
    return "(" + x + "," + y + "," + z + ")";
  }
}
