package edu.kit.ifv.mobitopp.util.parameter;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class LogitParametersTest {

  @Test
  void filterElements() throws Exception {
    String parameter = "excluded";
    Map<String, Double> parameters = singletonMap(parameter, 1.0d);
    LogitParameters original = new LogitParameters(parameters);
    
    LogitParameters filtered = original.filter(p -> !parameter.equals(p));
    
    assertThat(filtered, is(equalTo(new LogitParameters(emptyMap()))));
  }
  
  @Test
	void combinesToParameterSets() throws Exception {
  	String first = "first";
  	String second = "second";
    LogitParameters some = new LogitParameters(singletonMap(first, 1.0d));
    LogitParameters other = new LogitParameters(singletonMap(second, 2.0d));
    
    LogitParameters filtered = some.combineWith(other);
    
    assertThat(filtered, is(equalTo(new LogitParameters(Map.of(first, 1.0d, second, 2.0d))))); 
	}
}
