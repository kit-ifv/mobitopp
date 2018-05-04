package edu.kit.ifv.mobitopp.routing;

import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;

public class NodeFromVisumNode
	extends DefaultNode
	implements Node
{

	private final VisumNode node;
	private final boolean target;


	public NodeFromVisumNode(VisumNode node) {

		super("N" + node.id());
		this.node = node;
		target = false;
	}

	public NodeFromVisumNode(VisumNode node, boolean target) {
		super("N" + node.id());
		this.node = node;
		this.target = target;
	}

	public VisumPoint2 coord() {

		assert node != null;
		assert node.coord != null;

		return node.coord;
	}

	public VisumNode visumNode() {
		return node;
	}

	@Override
	public boolean isTarget() {
		return target;
	}

}
