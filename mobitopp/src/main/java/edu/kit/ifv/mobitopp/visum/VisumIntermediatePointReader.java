package edu.kit.ifv.mobitopp.visum;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumIntermediatePointReader extends VisumBaseReader {

  private final Map<Integer,SortedMap<Integer,VisumPoint>> result;

  public VisumIntermediatePointReader(NetfileLanguage language) {
    super(language);
    result = new HashMap<>();
  }

  public Map<Integer, SortedMap<Integer, VisumPoint>> readPoints(Stream<Row> content) {
    result.clear();
    content.forEach(this::read);
    return result;
  }

  public void read(Row row) {
    int edgeId = edgeId(row);
    int index =  index(row);
    VisumPoint point = createPoint(row);
    if (!result.containsKey(edgeId)) {
      result.put(edgeId, new TreeMap<>());
    }
    result.get(edgeId).put(index,point);
  }

  public int edgeId(Row row) {
    return row.valueAsInteger(edgeId());
  }

  public int index(Row row) {
    return row.valueAsInteger(index());
  }

  public VisumPoint createPoint(Row row) {
    float x = row.valueAsFloat(xCoord());
    float y = row.valueAsFloat(yCoord());
    return new VisumPoint(x,y);
  }

}
