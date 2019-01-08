package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.ResultWriter;

public class SerialiseDemography {

  private final DemographyCsv demographyCsv;
  private final ResultWriter resultWriter;

  public SerialiseDemography(DemandZoneRepository zoneRepository, ResultWriter resultWriter) {
    super();
    this.resultWriter = resultWriter;
    demographyCsv = new DemographyCsv(zoneRepository);
  }

  public void serialiseDemography() {
    serialiseActual();
    serialiseNominal();
  }

  private void serialiseActual() {
    List<String> header = demographyCsv.createHeader();
    Category actualDemography = new Category("actualDemography", header);
    Consumer<String> toResults = line -> resultWriter.write(actualDemography, line);
    demographyCsv.serialiseActual(toResults);
  }

  private void serialiseNominal() {
    List<String> header = demographyCsv.createHeader();
    Category actualDemography = new Category("nominalDemography", header);
    Consumer<String> toResults = line -> resultWriter.write(actualDemography, line);
    demographyCsv.serialiseNominal(toResults);
  }

}
