package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultAttributeResolver implements AttributeResolver {

  private final List<Attribute> attributes;

  @Override
  public List<Attribute> attributesOf(AttributeType attributeType) {
    return attributes
        .stream()
        .filter(attribute -> attribute.type().equals(attributeType))
        .sorted(Comparator.comparing(Attribute::name))
        .collect(toList());
  }

}
