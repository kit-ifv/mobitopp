package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.collections.StreamUtils;
import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumSurfaceReader extends VisumBaseReader {

  private final Map<Integer, VisumFace> rings;

  public VisumSurfaceReader(NetfileLanguage language, Map<Integer, VisumFace> rings) {
    super(language);
    this.rings = rings;
  }

  public SortedMap<Integer, VisumSurface> readSurfaces(Stream<Row> content) {
    Map<Integer, List<RingInfo>> tmp = content
        .map(this::createRingInfo)
        .collect(groupingBy(r -> r.areaId, TreeMap::new, toList()));

    return tmp
        .entrySet()
        .stream()
        .map(this::createSurface)
        .collect(toMap(s -> s.id, Function.identity(), StreamUtils.throwingMerger(), TreeMap::new));
  }

  public VisumSurface createSurface(Entry<Integer, List<RingInfo>> entry) {
    int id = entry.getKey();
    List<VisumFace> faces = entry
        .getValue()
        .stream()
        .map(r -> rings.get(r.ringId))
        .collect(toList());
    List<Integer> enclave = entry.getValue().stream().map(r -> r.enclave).collect(toList());
    return new VisumSurface(id, faces, enclave);
  }

  public RingInfo createRingInfo(Row row) {
    int areaId = row.valueAsInteger(areaId());
    int ringId = row.valueAsInteger(ringId());
    int enclave = row.valueAsInteger(attribute(StandardAttributes.enclave));
    return new RingInfo(areaId, ringId, enclave);
  }

  private static final class RingInfo {

    private final int areaId;
    private final int ringId;
    private final int enclave;

    public RingInfo(int areaId, int ringId, int enclave) {
      super();
      this.areaId = areaId;
      this.ringId = ringId;
      this.enclave = enclave;
    }

  }
}
