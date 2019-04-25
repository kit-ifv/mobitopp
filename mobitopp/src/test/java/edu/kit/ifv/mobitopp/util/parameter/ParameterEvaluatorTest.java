package edu.kit.ifv.mobitopp.util.parameter;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import javax.script.ScriptException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParameterEvaluatorTest {

  private ParameterEvaluator evaluator;

  @BeforeEach
  public void initialise() {
    evaluator = new ParameterEvaluator();
  }

  @Test
  void parsesEmptyDoubleValue() throws Exception {
    double result = evaluator.evaluateAsDouble(" ");

    assertThat(result, is(0.0d));
  }

  @Test
  void parsesIntAsDouble() throws Exception {
    String integerParameter = "2";

    double result = evaluator.evaluateAsDouble(integerParameter);

    assertThat(result, is(2.0d));
  }

  @Test
  public void parseSingleValue() {
    String parameter = "-0.5";

    double result = evaluator.evaluateAsDouble(parameter);

    assertThat(result, is(equalTo(-0.5d)));
  }

  @Test
  public void parseParameterAsFormular() throws ScriptException {
    String parameter = "-0.5 -0.10";

    double result = evaluator.evaluateAsDouble(parameter);

    assertThat(result, is(equalTo(-0.6d)));
  }

  @Test
  void parsesEmptyIntValue() throws Exception {
    int result = evaluator.evaluateAsInt(" ");

    assertThat(result, is(0));
  }

  @Test
  public void evaluateAsInt() {
    String integerParameter = "2";

    int result = evaluator.evaluateAsInt(integerParameter);

    assertThat(result, is(2));
  }

  @Test
  public void failsWithWrongFormular() {
    assertThrows(IllegalArgumentException.class, () -> evaluator.evaluateAsDouble("x"));
  }
}
