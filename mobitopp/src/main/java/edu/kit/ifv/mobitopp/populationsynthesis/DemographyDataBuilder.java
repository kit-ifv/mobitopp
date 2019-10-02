package edu.kit.ifv.mobitopp.populationsynthesis;

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

public class DemographyDataBuilder {

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
    InMemoryData data = new InMemoryData();
    for (Entry<String, String> entry : sorted.entrySet()) {
      String filePath = entry.getValue();
      StructuralData structuralData = createStructuralData(filePath);
      String attributeName = entry.getKey();
      AttributeType type = createType(attributeName);
      data.store(type, structuralData);
    }
    return data;
  }

  private AttributeType createType(String attributeName) {
    return Arrays
        .stream(StandardAttribute.values())
        .filter(type -> attributeName.equals(type.attributeName()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Can not find attribute with name: %s", attributeName)));
  }

  protected StructuralData createStructuralData(String filePath) {
    File file = Convert.asFile(filePath);
    return dataFactory.createData(file);
  }

}
