package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
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
    return file -> new StructuralData(new CsvFile(file));
  }

  public DemographyData build() {
    Map<String, String> input = configuration.getDemographyData();
    InMemoryData data = new InMemoryData();
    for (Entry<String, String> entry : input.entrySet()) {
      String filePath = entry.getValue();
      File asFile = Convert.asFile(filePath);
      StructuralData structuralData = createStructuralData(asFile);
      String type = entry.getKey();
      data.store(type, structuralData);
    }
    return data;
  }

  protected StructuralData createStructuralData(File file) {
    return dataFactory.createData(file);
  }

}
