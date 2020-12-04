package edu.kit.ifv.mobitopp.util.logit;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;


public class DefaultLogitModel<T>
	implements LogitModel<T>
{



	public Map<T,Double> calculateProbabilities(
		Map<T,Double> utilities
	) {

		return calculateProbabilities(1.0, utilities, utilities.keySet());
	}

	public Map<T,Double> calculateProbabilities(
		Map<T,Double> utilities, 
		Collection<T> choiceSet
	) {

		return calculateProbabilities(1.0, utilities, choiceSet);
	}


	public Map<T,Double> calculateProbabilities(
		Double beta,
		Map<T,Double> utils, 
		Collection<T> choiceSet
	) {

		Map<T,Double> utilities = new LinkedHashMap<>(utils);

		utilities.keySet().retainAll(choiceSet);

		assert !utilities.isEmpty() : (choiceSet + " - " + utils);

		Map<T,Double> expUtilities = calculateExpUtilities(beta, utilities, 0.0);

		Double total = sum(expUtilities);

		if (total == 0.0) {
			Double maxVal = Collections.max(utilities.values());

			assert !maxVal.isNaN();
			assert maxVal < 0.0;
		
			expUtilities = calculateExpUtilities(beta, utilities, maxVal);
			total = sum(expUtilities);
		}

		assert total > 0.0 : ("sum(expUtilities)=" + total + "\n" 
																								+ "choiceSet:\n" + choiceSet + "\n"
																								+ "utilities:\n" + utilities);
		assert !total.isNaN();
		assert !total.isInfinite();

		Map<T,Double> probabilities = new LinkedHashMap<T, Double>(); 

		for (T choice: expUtilities.keySet()) {

			Double expVal = expUtilities.get(choice);

			Double probability = expVal/total;

			probabilities.put(choice, probability);
		}

		return probabilities;
	}


	private Map<T,Double> calculateExpUtilities(
		double beta,
		Map<T,Double> utilities,
		double offset
	) {

		Map<T,Double> expUtilities = new LinkedHashMap<T,Double>();

		for (T choice : utilities.keySet()) {

			assert utilities.containsKey(choice);

			Double val = utilities.get(choice) + offset;

			assert !(val.isInfinite() && val > 0.0);

			Double expVal = Math.exp(beta*val);

			expUtilities.put(choice, expVal);
		}

		if (sum(expUtilities).isInfinite()) {
			return calculateExpUtilities(beta/2.0, utilities, offset);
		}
		
		if (sum(expUtilities) == 0.0) {
			return calculateExpUtilities(beta/2.0, utilities, offset);
		}


		return expUtilities;
	}

	private Double sum(Map<T,Double> data) {

		Double total = 0.0;

		for (Double val : data.values()) {

			total += val;
		}

		return total;
	}


	public T select(
		Map<T,Double> utilities, 
		Collection<T> choiceSet,
		Double randomNumber
	) {

		assert !utilities.isEmpty();

		Map<T,Double> probabilities = calculateProbabilities(utilities, choiceSet);

		DiscreteRandomVariable<T> distribution = new DiscreteRandomVariable<T>(probabilities);

		return distribution.realization(randomNumber);
	}

	public T select(
		Map<T,Double> utilities, 
		Double randomNumber
	) {

		return select(utilities, utilities.keySet(), randomNumber);
	}



}

