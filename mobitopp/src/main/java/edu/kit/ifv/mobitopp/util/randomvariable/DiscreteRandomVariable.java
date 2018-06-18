package edu.kit.ifv.mobitopp.util.randomvariable;

import java.util.Map;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class DiscreteRandomVariable<T> {

	private final NavigableMap<Double,T> cumulativeDistribution;


	public DiscreteRandomVariable(Map<T,? extends Number> distribution) {

		this.cumulativeDistribution = cumulativeDistribution(distribution);
	}

	public DiscreteRandomVariable(Collection<T> distribution) {

		this.cumulativeDistribution = cumulativeUniformDistribution(distribution);
	}

	protected NavigableMap<Double,T> cumulativeDistribution(Map<T,? extends Number> distribution) {

		assert distribution.size() > 0;

		NavigableMap<Double,T> cumul = new TreeMap<Double,T>();

		Double total = 0.0;

		for (Number val :  distribution.values()) {
			total += val.doubleValue();
		}

		if (total == 0.0) return cumulativeUniformDistribution(distribution.keySet());

		assert total > 0.0 : distribution;

		if (total.isInfinite()) {

			Set<T> infinite = infiniteValues(distribution);

			return cumulativeUniformDistribution(infinite);
		}

		assert !total.isInfinite();

		Double current = 0.0;

		for (T t: distribution.keySet()) {
			Number num = distribution.get(t);
			double val = num.doubleValue();

			double increment = val/total;	

			if (current + increment > current) {
				current += increment;

				cumul.put(current,t);
			}
		}

		assert cumul.size() > 0 : distribution;

		return cumul;
	}

	protected Set<T> infiniteValues(Map<T,? extends Number> distribution) {

		Set<T> result = new LinkedHashSet<T>();

		for (T key: distribution.keySet()) {

			Double value = distribution.get(key).doubleValue();

			if (value.isInfinite()) {
				result.add(key);
			}
		}

		return result;
	}

	protected NavigableMap<Double,T> cumulativeUniformDistribution(Collection<T> distribution) {

		assert distribution.size() > 0;

		NavigableMap<Double,T> cumul = new TreeMap<Double,T>();

		Double current = 0.0;

		double increment = 1.0/distribution.size();

		for (T t: distribution) {

			current += increment;

			cumul.put(current,t);
		}


		assert cumul.size() > 0;

		return cumul;
	}


	public T realization(double rnd) {

		assert rnd >= 0.0;
		assert rnd <= 1.0;

		Map.Entry<Double,T> entry = cumulativeDistribution.higherEntry(rnd);

		return entry != null ? entry.getValue() :  cumulativeDistribution.lastEntry().getValue();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cumulativeDistribution == null) ? 0 : cumulativeDistribution.hashCode());
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
		@SuppressWarnings("rawtypes")
		DiscreteRandomVariable other = (DiscreteRandomVariable) obj;
		if (cumulativeDistribution == null) {
			if (other.cumulativeDistribution != null)
				return false;
		} else if (!cumulativeDistribution.equals(other.cumulativeDistribution))
			return false;
		return true;
	}

	public String toString() {
		return this.cumulativeDistribution.toString();
	}

}

