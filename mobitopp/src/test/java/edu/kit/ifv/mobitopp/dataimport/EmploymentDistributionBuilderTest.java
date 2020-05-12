package edu.kit.ifv.mobitopp.dataimport;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class EmploymentDistributionBuilderTest {

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();

	private StructuralData demographyData;
	private EmploymentDistributionBuilder builder;
	private File emptyFile;

	@Before
	public void initialise() throws IOException {
		createFileWithHeader();
		demographyData = Example.demographyData();
		builder = new EmploymentDistributionBuilder(demographyData, StandardAttribute.employment);
	}

	private void createFileWithHeader() throws IOException {
		emptyFile = baseFolder.newFile();
		Files.write(emptyFile.toPath(), "Header".getBytes());
	}

	@Test
	public void build() {
		String zoneId = "1";
    EmploymentDistribution distribution = builder.build(zoneId);

		assertThat(distribution).isEqualTo(expectedEmployment());
	}

	@Test
	public void buildWithMissingData() {
		StructuralData data = missingData();
    String zoneId = "1";
		EmploymentDistributionBuilder builder = new EmploymentDistributionBuilder(data, StandardAttribute.employment);

		EmploymentDistribution distribution = builder.build(zoneId);

		assertThat(distribution).isEqualTo(new EmploymentDistribution());
	}

	private StructuralData missingData() {
		return new StructuralData(CsvFile.createFrom(emptyFile.getAbsolutePath()));
	}

	private EmploymentDistribution expectedEmployment() {
		EmploymentDistribution distribution = new EmploymentDistribution();
		distribution.addItem(new EmploymentDistributionItem(Employment.FULLTIME, 1456));
		distribution.addItem(new EmploymentDistributionItem(Employment.PARTTIME, 515));
		distribution.addItem(new EmploymentDistributionItem(Employment.NONE, 395));
		distribution.addItem(new EmploymentDistributionItem(Employment.STUDENT_TERTIARY, 97));
		distribution.addItem(new EmploymentDistributionItem(Employment.STUDENT_SECONDARY, 474));
		distribution.addItem(new EmploymentDistributionItem(Employment.STUDENT_PRIMARY, 174));
		distribution.addItem(new EmploymentDistributionItem(Employment.EDUCATION, 120));
		distribution.addItem(new EmploymentDistributionItem(Employment.RETIRED, 1035));
		distribution.addItem(new EmploymentDistributionItem(Employment.INFANT, 0));
		return distribution;
	}

}
