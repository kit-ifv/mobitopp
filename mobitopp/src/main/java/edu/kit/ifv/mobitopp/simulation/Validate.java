package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.IOException;

import edu.kit.ifv.mobitopp.data.local.Convert;
import lombok.extern.slf4j.Slf4j;
import edu.kit.ifv.mobitopp.data.local.TypeMapping;

@Slf4j
public class Validate implements Validation {

	private final TypeMapping modeToType;
	private WrittenConfiguration configuration;

	public Validate(final TypeMapping modeToType) {
		super();
		this.modeToType = modeToType;
	}

	@Override
	public void now(WrittenConfiguration configuration) throws IOException {
		this.configuration = configuration;
		validateThreadCount();
		validateTimeStepLength();
		validateNumberOfZones();
		validateFractionOfPopulation();
		validateDestinationChoiceFiles();
		validateDataSource();
	}

	private void validateThreadCount() {
		if (1 > configuration.getThreadCount()) {
			throw warn(new IllegalArgumentException(
					"Thread count must be at least 1. Specified number of threads: " + configuration.getThreadCount()), log);
		}
	}
	
	private void validateTimeStepLength() {
		if (WrittenConfiguration.defaultTimeStepLength != configuration.getTimeStepLength()) {
			throw warn(new IllegalArgumentException(
					"Time step length must remain at default value. Other values may produce undesired results."), log);
		}
	}

	private void validateNumberOfZones() {
		if (1 > configuration.getNumberOfZones()) {
			throw warn(new IllegalArgumentException(
					"There must be at least one zone to process. Specified number of zones: "
							+ configuration.getNumberOfZones()), log);
		}
	}

	private void validateFractionOfPopulation() {
		if (0.0 > configuration.getFractionOfPopulation()) {
			throw warn(new IllegalArgumentException("Can not simulation less than 0% of population."), log);
		}
		if (1.0 < configuration.getFractionOfPopulation()) {
			throw warn(new IllegalArgumentException("Can not simulation more than 1000% of population."), log);
		}
	}

	private void validateDestinationChoiceFiles() {
		for (String path : configuration.getDestinationChoice().values()) {
			if (isMissing(path)) {
				throw warn(new IllegalArgumentException("Destination choice file is missing at path: " + path), log);
			}
		}
	}

	private boolean isMissing(String pathname) {
		return !Convert.asFile(pathname).exists();
	}

	private void validateDataSource() throws IOException {
		configuration.getDataSource().validate(modeToType);
	}

}
