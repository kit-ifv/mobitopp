package edu.kit.ifv.domain.synthesis.attributes.person

interface HasTransitPass {
    val hasTransitPass: Boolean
}

interface HasMutableTransitPass : HasTransitPass {
    override var hasTransitPass: Boolean
}
