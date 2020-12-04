package edu.kit.ifv.mobitopp.util.logit;

import java.util.Collection;
import java.util.Map;

public interface LogitModel<T>
{

	public Map<T,Double> calculateProbabilities(
		Map<T,Double> utilities
	);

	public Map<T,Double> calculateProbabilities(
		Map<T,Double> utilities, 
		Collection<T> choiceSet
	);

	public Map<T,Double> calculateProbabilities(
		Double beta,
		Map<T,Double> utils, 
		Collection<T> choiceSet
	);

	public T select(
		Map<T,Double> utilities, 
		Collection<T> choiceSet,
		Double randomNumber
	);

	public T select(
		Map<T,Double> utilities, 
		Double randomNumber
	);


}

