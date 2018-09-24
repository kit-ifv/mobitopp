package edu.kit.ifv.mobitopp.util.parameter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ParameterEvaluator {

	public double evaluateAsDouble(String parameter) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		try {
			return (double) engine.eval(parameter);
		} catch (ScriptException cause) {
			throw new IllegalArgumentException("Cannot parse parameter: " + parameter);
		}
	}

	public int evaluateAsInt(String parameter) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		try {
			return (int) engine.eval(parameter);
		} catch (ScriptException cause) {
			throw new IllegalArgumentException("Cannot parse parameter: " + parameter);
		}
	}

}
