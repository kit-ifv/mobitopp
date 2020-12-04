package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VisumLinks
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final Map<Integer, VisumLink> links;


	private static Map<VisumNode,Map<VisumNode,VisumOrientedLink>> mappedLinks;



	public VisumLinks(Map<Integer, VisumLink> links) {

		this.links = Collections.unmodifiableMap(links);

		initMappedLinks(links);
	}


	private static void initMappedLinks(Map<Integer, VisumLink> links) {

		mappedLinks = new HashMap<VisumNode,Map<VisumNode,VisumOrientedLink>>();

		for (VisumLink link : links.values()) {

			if (!mappedLinks.containsKey(link.linkA.from)) {
					mappedLinks.put(link.linkA.from, new HashMap<VisumNode,VisumOrientedLink>());
			}

			mappedLinks.get(link.linkA.from).put(link.linkA.to, link.linkA);

			if (!mappedLinks.containsKey(link.linkB.from)) {
					mappedLinks.put(link.linkB.from, new HashMap<VisumNode,VisumOrientedLink>());
			}

			mappedLinks.get(link.linkB.from).put(link.linkB.to, link.linkB);

		}
	}


	public VisumOrientedLink getOrientedLink(VisumNode from, VisumNode to) {

		return mappedLinks.get(from).get(to);	
	}

  @Override
  public int hashCode() {
    return Objects.hash(links);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumLinks other = (VisumLinks) obj;
    return Objects.equals(links, other.links);
  }

  @Override
  public String toString() {
    return "VisumLinks [links=" + links + "]";
  }
	
}
