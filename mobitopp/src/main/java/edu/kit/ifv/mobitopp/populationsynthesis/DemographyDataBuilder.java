package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemographyDataBuilder {

  static final String seperator = ":";
	private final WrittenConfiguration configuration;
  private final DataFactory dataFactory;

  public DemographyDataBuilder(WrittenConfiguration configuration, DataFactory dataFactory) {
    super();
    this.configuration = configuration;
    this.dataFactory = dataFactory;
  }

  public DemographyDataBuilder(WrittenConfiguration configuration) {
    this(configuration, defaultFactory());
  }

  private static DataFactory defaultFactory() {
    return file -> new StructuralData(CsvFile.createFrom(file));
  }

  public DemographyData build() {
    Map<String, String> input = configuration.getDemographyData();
    TreeMap<String, String> sorted = new TreeMap<>(input);
    InMemoryData toData = new InMemoryData();
    for (Entry<String, String> entry : sorted.entrySet()) {
      add(entry, toData);
    }
    return toData;
  }

	private void add(Entry<String, String> entry, InMemoryData data) {
		String filePath = entry.getValue();
		StructuralData structuralData = createStructuralData(filePath);
		String attributeName = entry.getKey();
		RegionalLevel level = createRegionalLevel(attributeName);
		AttributeType type = createType(attributeName);
		data.store(level, type, structuralData);
	}

  private RegionalLevel createRegionalLevel(String attributeName) {
  	String[] split = attributeName.split(seperator);
  	if (1 == split.length) {
  		return RegionalLevel.zone;
  	}
		return RegionalLevel.levelOf(split[0]);
	}

	private AttributeType createType(String attribute) {
		String[] split = attribute.split(seperator);
		String attributeName = split[split.length - 1];
    return Arrays
        .stream(StandardAttribute.values())
        .filter(type -> attributeName.equals(type.attributeName()))
        .findFirst()
        .orElseThrow(() -> warn(new IllegalArgumentException(
            String.format("Can not find attribute with name: %s", attributeName)), log));
  }

  protected StructuralData createStructuralData(String filePath) {
    File file = Convert.asFile(filePath);
    return dataFactory.createData(file);
  }

}
