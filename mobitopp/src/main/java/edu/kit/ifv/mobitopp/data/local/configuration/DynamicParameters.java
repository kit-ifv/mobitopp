package edu.kit.ifv.mobitopp.data.local.configuration;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.local.Convert;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is a wrapper around a map which allows a simple conversion for values into primitive
 * types.
 * 
 * @author bp1110
 */
@Slf4j
public class DynamicParameters {

  private final Map<String, String> parameters;

  public DynamicParameters(Map<String, String> parameters) {
    super();
    this.parameters = parameters;
  }

  public String value(String parameter) {
    verifyExisting(parameter);
    return parameters.get(parameter).toString();
  }

  private void verifyExisting(String parameter) {
    if (!hasValue(parameter)) {
      throw warn(new IllegalArgumentException(
          "Dynamic parameter missing: " + parameter + " containing: " + parameters.toString()), log);
    }
  }

  public boolean hasValue(String parameter) {
    return parameters.containsKey(parameter);
  }

  public long valueAsLong(String parameter) {
    return Long.parseLong(value(parameter));
  }

  public int valueAsInteger(String parameter) {
    return Integer.parseInt(value(parameter));
  }

  public double valueAsDouble(String parameter) {
    return Double.parseDouble(value(parameter));
  }

  public float valueAsFloat(String parameter) {
    return Float.parseFloat(value(parameter));
  }

  public boolean valueAsBoolean(String parameter) {
    return Boolean.parseBoolean(value(parameter));
  }

  public File valueAsFile(String parameter) {
    return Convert.asFile(value(parameter));
  }

  public Map<String, String> toMap() {
    return Collections.unmodifiableMap(parameters);
  }

  @Override
  public String toString() {
    return getClass().getName() + " [parameters=" + parameters + "]";
  }

}
