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
}
