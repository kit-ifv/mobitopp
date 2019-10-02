package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.util.TreeMap;
import java.util.stream.IntStream;

import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.dataimport.RangeItemParser;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class EconomicalStatusDistributionParser {

	private TreeMap<Double, RangeDistributionIfc> distributions;
	private CsvFile csvFile;
	private RangeItemParser itemParser;

	public EconomicalStatusDistributionParser() {
		super();
		itemParser = new RangeItemParser("economical_status:");
	}

	public TreeMap<Double, RangeDistributionIfc> parse(File input) {
		csvFile = createCsvFile(input);
		distributions = new TreeMap<>();
		IntStream.range(0, csvFile.getLength()).forEach(this::parse);
		return distributions;
	}

	private void parse(int rowNumber) {
		RangeDistribution distribution = new RangeDistribution();
		csvFile
				.getAttributes()
				.stream()
				.skip(1)
				.map(c -> toRangeItem(rowNumber, c))
				.forEach(distribution::addItem);
		double householdSize = csvFile.getDouble(rowNumber, "household_size");
		distributions.put(householdSize, distribution);
	}

	private RangeDistributionItem toRangeItem(int rowNumber, String column) {
		int value = csvFile.getInteger(rowNumber, column);
		return itemParser.parse(value, column);
	}

	CsvFile createCsvFile(File input) {
		return new CsvFile(input);
	}

}
