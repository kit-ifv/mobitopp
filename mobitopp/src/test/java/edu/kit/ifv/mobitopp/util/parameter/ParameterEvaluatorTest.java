package edu.kit.ifv.mobitopp.util.parameter;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.util.parameter.ParameterEvaluator;

public class ParameterEvaluatorTest {

	private ParameterEvaluator evaluator;

	@Before
	public void initialise() {
		evaluator = new ParameterEvaluator();
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
	public void evaluateAsInt() {
		String integerParameter = "2";
		
		int result = evaluator.evaluateAsInt(integerParameter);
		
		assertThat(result, is(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsWithWrongFormular() {
		evaluator.evaluateAsDouble("x");
	}
}
