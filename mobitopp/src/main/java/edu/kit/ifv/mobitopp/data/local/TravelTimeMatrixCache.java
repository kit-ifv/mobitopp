package edu.kit.ifv.mobitopp.data.local;

import java.io.IOException;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedTravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixId;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class TravelTimeMatrixCache extends MatrixCache<TravelTimeMatrixId, TaggedTravelTimeMatrix> {

  private TypeMapping modeToType;

  public TravelTimeMatrixCache(
      MatrixConfiguration configuration, TypeMapping modeToType) {
    super(configuration);
    this.modeToType = modeToType;
  }

  public TravelTimeMatrixType typeOf(Mode mode) {
    return modeToType.resolve(mode);
  }

  public TravelTimeMatrixId idOf(TravelTimeMatrixType matrixType, Time date) {
    return configuration().idOf(matrixType, date);
  }

  public TravelTimeMatrix matrixFor(Mode mode, Time date) {
    TravelTimeMatrixId id = idOf(typeOf(mode), date);
    return matrixFor(id).matrix();
  }

  protected TaggedTravelTimeMatrix loadMatrixBy(TravelTimeMatrixId id) throws IOException {
    return configuration().travelTimeMatrixFor(id);
  }

  @Override
  protected Stream<TravelTimeMatrixId> split(TaggedTravelTimeMatrix taggedMatrix) {
    return taggedMatrix.id().split();
  }

}
