package edu.kit.ifv.mobitopp.data;

public class Value {

  private final String value;

  public Value(String value) {
    this.value = value;
  }
  
  public long asLong() {
  	return Long.parseLong(value);
  }

  public int asInt() {
    return Integer.parseInt(value);
  }

  public short asShort() {
    return Short.parseShort(value);
  }
  
  public boolean asBoolean() {
    return Boolean.parseBoolean(value);
  }
  
  public float asFloat() {
    return Float.valueOf(value);
  }
  
  public double asDouble() {
  	return Double.valueOf(value);
  }

  public String asString() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    Value other = (Value) obj;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return value;
  }

}
