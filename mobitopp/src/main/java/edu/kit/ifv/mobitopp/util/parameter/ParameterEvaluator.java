package edu.kit.ifv.mobitopp.util.parameter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ParameterEvaluator {
	
	private static final String ENGINE_NAME = "rhino";

	public double evaluateAsDouble(String parameter) {
	  if (parameter.trim().isEmpty()) {
	    return 0.0d;
	  }
		ScriptEngine engine = new ScriptEngineManager().getEngineByName(ENGINE_NAME);
		try {
			Object eval = engine.eval(parameter);
			if (eval instanceof Double) {
			  return (double) eval;
			}
			return ((Long) engine.eval(parameter)).intValue();
		} catch (ScriptException cause) {
			throw new IllegalArgumentException("Cannot parse parameter: " + parameter);
		}
	}

	public int evaluateAsInt(String parameter) {
    if (parameter.trim().isEmpty()) {
      return 0;
    }
		ScriptEngine engine = new ScriptEngineManager().getEngineByName(ENGINE_NAME);
		try {
			return ((Long) engine.eval(parameter)).intValue();
		} catch (ScriptException cause) {
			throw new IllegalArgumentException("Cannot parse parameter: " + parameter);
		}
	}

}
