package edu.kit.ifv.mobitopp.util.dataimport;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;

public class DynamicNumberRepository implements AreaTypeRepository {

  static final DynamicNumberType defaultType = new DynamicNumberType(-1);
  private final Map<Integer, AreaType> types;

  public DynamicNumberRepository() {
    super();
    types = new HashMap<>();
  }

  @Override
  public AreaType getTypeForCode(int code) {
    if (types.containsKey(code)) {
      return types.get(code);
    }
    DynamicNumberType type = new DynamicNumberType(code);
    types.put(code, type);
    return type;
  }

  @Override
  public AreaType getTypeForName(String name) {
    int code = Integer.parseInt(name);
    return getTypeForCode(code);
  }

  @Override
  public AreaType getDefault() {
    return defaultType;
  }

}
