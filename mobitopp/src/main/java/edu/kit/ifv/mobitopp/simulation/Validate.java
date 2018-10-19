package edu.kit.ifv.mobitopp.simulation;

import java.io.IOException;

import edu.kit.ifv.mobitopp.data.local.Convert;

public class Validate implements Validation {

	private WrittenConfiguration configuration;

	public Validate() {
		super();
	}

	@Override
	public void now(WrittenConfiguration configuration) throws IOException {
		this.configuration = configuration;
		validateTimeStepLength();
		validateNumberOfZones();
		validateFractionOfPopulation();
		validateDestinationChoiceFiles();
		validateDataSource();
	}

	private void validateTimeStepLength() {
		if (WrittenConfiguration.defaultTimeStepLength != configuration.getTimeStepLength()) {
			throw new IllegalArgumentException(
					"Time step length must remain at default value. Other values may produce undesired results.");
		}
	}

	private void validateNumberOfZones() {
		if (1 > configuration.getNumberOfZones()) {
			throw new IllegalArgumentException(
					"There must be at least one zone to process. Specified number of zones: "
							+ configuration.getNumberOfZones());
		}
	}

	private void validateFractionOfPopulation() {
		if (0.0 > configuration.getFractionOfPopulation()) {
			throw new IllegalArgumentException("Can not simulation less than 0% of population.");
		}
		if (1.0 < configuration.getFractionOfPopulation()) {
			throw new IllegalArgumentException("Can not simulation more than 1000% of population.");
		}
	}

	private void validateDestinationChoiceFiles() {
		for (String path : configuration.getDestinationChoice().values()) {
			if (isMissing(path)) {
				throw new IllegalArgumentException("Destination choice file is missing at path: " + path);
			}
		}
	}

	private boolean isMissing(String pathname) {
		return !Convert.asFile(pathname).exists();
	}

	private void validateDataSource() throws IOException {
		configuration.getDataSource().validate();
	}

}
