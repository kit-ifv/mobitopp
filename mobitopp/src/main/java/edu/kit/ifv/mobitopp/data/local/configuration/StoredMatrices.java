package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class StoredMatrices {

	private DynamicTimeMatrices<CostMatrixType> costMatrices;
	private DynamicTimeMatrices<TravelTimeMatrixType> travelTimeMatrices;
	private StaticTimeMatrices<ActivityType> fixedDistributionTypes;

	public StoredMatrices() {
		super();
		costMatrices = new DynamicTimeMatrices<>();
		fixedDistributionTypes = new StaticTimeMatrices<>();
		travelTimeMatrices = new DynamicTimeMatrices<>();
	}

	public Map<CostMatrixType, TypeMatrices> getCostMatrices() {
		return costMatrices.asMap();
	}

	public void setCostMatrices(Map<CostMatrixType, TypeMatrices> costMatrices) {
		this.costMatrices = new DynamicTimeMatrices<>(costMatrices);
	}

	public void add(CostMatrixType matrixType, DayType dayType, TimeSpan timeSpan, String path) {
		costMatrices.add(matrixType, dayType, timeSpan, path);
	}

	public TypeMatrices costMatrixFor(CostMatrixType matrixType) {
		return costMatrices.matrixFor(matrixType);
	}

	public Map<ActivityType, String> getFixedDistributionMatrices() {
		return fixedDistributionTypes.asMap();
	}

	public void setFixedDistributionMatrices(Map<ActivityType, String> matrices) {
		this.fixedDistributionTypes = new StaticTimeMatrices<>(matrices);
	}

	public void add(ActivityType matrixType, String path) {
		fixedDistributionTypes.add(matrixType, path);
	}

	public StoredMatrix fixedDistributionMatrixFor(ActivityType matrixType) {
		return fixedDistributionTypes.matrixFor(matrixType);
	}

	public Map<TravelTimeMatrixType, TypeMatrices> getTravelTimeMatrices() {
		return travelTimeMatrices.asMap();
	}

	public void setTravelTimeMatrices(Map<TravelTimeMatrixType, TypeMatrices> travelTimeMatrices) {
		this.travelTimeMatrices = new DynamicTimeMatrices<>(travelTimeMatrices);
	}

	public void add(
			TravelTimeMatrixType matrixType, DayType dayType, TimeSpan timeSpan, String path) {
		travelTimeMatrices.add(matrixType, dayType, timeSpan, path);
	}

	public TypeMatrices travelTimeMatrixFor(TravelTimeMatrixType matrixType) {
		return travelTimeMatrices.matrixFor(matrixType);
	}

	public void forEach(Consumer<StoredMatrix> consumer) {
		costMatrices
				.asMap()
				.values()
				.stream()
				.map(TypeMatrices::getAt)
				.map(Map::values)
				.flatMap(Collection::stream)
				.flatMap(this::storedMatrices)
				.forEach(consumer);
	}
	
	public Stream<StoredMatrix> storedMatrices(DayTypeMatrices matrices) {
		return matrices.getBetween().keySet().stream().map(matrices::in);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((costMatrices == null) ? 0 : costMatrices.hashCode());
		result = prime * result + ((travelTimeMatrices == null) ? 0 : travelTimeMatrices.hashCode());
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
		StoredMatrices other = (StoredMatrices) obj;
		if (costMatrices == null) {
			if (other.costMatrices != null)
				return false;
		} else if (!costMatrices.equals(other.costMatrices))
			return false;
		if (travelTimeMatrices == null) {
			if (other.travelTimeMatrices != null)
				return false;
		} else if (!travelTimeMatrices.equals(other.travelTimeMatrices))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StoredMatrices [costMatrices=" + costMatrices + ", travelTimeMatrices="
				+ travelTimeMatrices + "]";
	}

}
