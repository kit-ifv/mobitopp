package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.MatrixParser;
import edu.kit.ifv.mobitopp.data.local.Validate;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumMatrixParser;

public class FileMatrixConfiguration implements MatrixConfiguration {

  private final StoredMatrices matrices;
  private final File baseFolder;

  FileMatrixConfiguration(StoredMatrices matrices, File baseFolder) {
    super();
    this.matrices = matrices;
    this.baseFolder = baseFolder;
  }

  public static MatrixConfiguration empty(File baseFolder) {
    return new FileMatrixConfiguration(new StoredMatrices(), baseFolder);
  }

  public static MatrixConfiguration from(File input, File baseFolder) throws FileNotFoundException {
    return from(new FileInputStream(input), baseFolder);
  }

  public static MatrixConfiguration from(InputStream input, File baseFolder) {
    MatrixConfigurationSerialiser serialiser = new MatrixConfigurationSerialiser();
    StoredMatrices matrices = serialiser.loadFrom(input);
    return new FileMatrixConfiguration(matrices, baseFolder);
  }

  private TypeMatrices costMatrixFor(CostMatrixType matrixType) {
    return matrices.costMatrixFor(matrixType);
  }

  @Override
  public TaggedCostMatrix costMatrixFor(CostMatrixId id) throws IOException {
    CostMatrixType type = id.matrixType();
    DayType dayType = id.dayType();
    TimeSpan timeSpan = id.timeSpan();
    StoredMatrix storedMatrix = costMatrixFor(type).at(dayType).in(timeSpan);
    CostMatrix matrix = load(storedMatrix);
    CostMatrixId storedId = new CostMatrixId(type, dayType, storedMatrix.timeSpan());
    return new TaggedCostMatrix(storedId, matrix);
  }

  @Override
  public CostMatrixId idOf(CostMatrixType matrixType, Time date) {
    DayType dayType = DayType.from(date);
    TimeSpan timeSpan = new TimeSpan(date.getHour());
    return new CostMatrixId(matrixType, dayType, timeSpan);
  }

  private CostMatrix load(StoredMatrix storedMatrix) throws IOException {
    return parserFor(storedMatrix).parseCostMatrix();
  }

  @Override
  public TravelTimeMatrixId idOf(TravelTimeMatrixType matrixType, Time date) {
    DayType dayType = DayType.from(date);
    TimeSpan timeSpan = new TimeSpan(date.getHour());
    return new TravelTimeMatrixId(matrixType, dayType, timeSpan);
  }

  private TypeMatrices travelTimeMatrixFor(TravelTimeMatrixType matrixType) {
    return matrices.travelTimeMatrixFor(matrixType);
  }

  @Override
  public TaggedTravelTimeMatrix travelTimeMatrixFor(TravelTimeMatrixId id) throws IOException {
    TravelTimeMatrixType type = id.matrixType();
    DayType dayType = id.dayType();
    TimeSpan timeSpan = id.timeSpan();
    StoredMatrix storedMatrix = travelTimeMatrixFor(type).at(dayType).in(timeSpan);
    TravelTimeMatrix matrix = loadTravelTimeMatrix(storedMatrix);
    TravelTimeMatrixId storedId = new TravelTimeMatrixId(type, dayType, storedMatrix.timeSpan());
    return new TaggedTravelTimeMatrix(storedId, matrix);
  }

  private TravelTimeMatrix loadTravelTimeMatrix(StoredMatrix in) throws IOException {
    return parserFor(in).parseTravelTimeMatrix();
  }

  MatrixParser parserFor(StoredMatrix storedMatrix) throws IOException {
    return VisumMatrixParser.load(storedMatrix.file(baseFolder));
  }

  @Override
  public TaggedFixedDistributionMatrix fixedDistributionMatrixFor(FixedDistributionMatrixId id)
      throws IOException {
    ActivityType type = id.matrixType();
    StoredMatrix fixedDistributionMatrixFor = matrices.fixedDistributionMatrixFor(type);
    FixedDistributionMatrix matrix = parserFor(fixedDistributionMatrixFor)
        .parseFixedDistributionMatrix();
    return new TaggedFixedDistributionMatrix(id, matrix);
  }

  @Override
  public FixedDistributionMatrixId idOf(ActivityType activityType) {
    return new FixedDistributionMatrixId(activityType);
  }

  @Override
  public void validate() {
    Validate.folder(baseFolder).isValid();
    Validate.matrices(matrices, baseFolder).areValid();
  }

}
