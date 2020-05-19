package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.ResultWriter;

public class SerialiseDemography {

	private final DemographyCsv demographyCsv;
	private final ResultWriter resultWriter;
	private final RegionalLevel regionalLevel;

	public SerialiseDemography(
			final List<AttributeType> attributeTypes,
			final Supplier<List<? extends DemandRegion>> demandSupplier, final ResultWriter resultWriter,
			final RegionalLevel regionalLevel) {
		super();
		this.resultWriter = resultWriter;
		this.regionalLevel = regionalLevel;
		demographyCsv = new DemographyCsv(attributeTypes, demandSupplier);
	}

	public void serialiseDemography() {
		serialiseActual();
		serialiseNominal();
	}

	private void serialiseActual() {
		List<String> header = demographyCsv.createHeader();
		Category actualDemography = new Category("actualDemography-" + regionalLevel.identifier(),
				header);
		Consumer<String> toResults = line -> resultWriter.write(actualDemography, line);
		demographyCsv.serialiseActual(toResults);
	}

	private void serialiseNominal() {
		List<String> header = demographyCsv.createHeader();
		Category actualDemography = new Category("nominalDemography-" + regionalLevel.identifier(),
				header);
		Consumer<String> toResults = line -> resultWriter.write(actualDemography, line);
		demographyCsv.serialiseNominal(toResults);
	}

}
