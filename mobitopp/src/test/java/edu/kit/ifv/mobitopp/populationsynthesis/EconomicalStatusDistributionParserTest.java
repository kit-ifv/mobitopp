package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class EconomicalStatusDistributionParserTest {

	@Test
	void buildUpDistribution() throws Exception {
		String content = "household_size;economical_status:0-499;economical_status:500-\r\n"
				+ "1,0;1;2\r\n" + "1,5;3;4\r\n";
		RangeDistribution oneDistribution = new RangeDistribution();
		oneDistribution.addItem(new RangeDistributionItem(0, 499, 1));
		oneDistribution.addItem(new RangeDistributionItem(500, Integer.MAX_VALUE, 2));
		RangeDistribution oneHalfDistribution = new RangeDistribution();
		oneHalfDistribution.addItem(new RangeDistributionItem(0, 499, 3));
		oneHalfDistribution.addItem(new RangeDistributionItem(500, Integer.MAX_VALUE, 4));
		TreeMap<Double, RangeDistributionIfc> expected = new TreeMap<>();
		expected.put(1.0d, oneDistribution);
		expected.put(1.5d, oneHalfDistribution);

		EconomicalStatusDistributionParser parser = createParser();
		TreeMap<Double, RangeDistributionIfc> distribution = parser.parse(content);

		assertThat(distribution).isEqualTo(expected);
	}

	private EconomicalStatusDistributionParser createParser() {
		return new EconomicalStatusDistributionParser() {

			@Override
			CsvFile createCsvFile(String content) {

				return new CsvFile("dummy-file-name") {

					@Override
					protected Reader createReader(File file) throws FileNotFoundException {
						return new InputStreamReader(new ByteArrayInputStream(content.toString().getBytes()));
					}
				};
			}
		};
	}
}
