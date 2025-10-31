package domain.synthesis.behavior.householdgeneration

open class GraphHierarchy<T>(protected val hierarchyGraph: MutableHierarchyGraph<T>) :
    MutableHierarchicElement<T> by hierarchyGraph
