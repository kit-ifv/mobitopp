package core.events

interface Subscribable<A> {
    val name: String
    val resources: Set<Resource<A>>

    fun availableResourcesFor(agent: A): Set<Resource<A>> =
        resources.filter { it.isAvailableFor(agent) }.toSet()
}

interface Resource<A> {

    fun isAvailableFor(agent: A): Boolean
}

interface SubscribableResource<A> : Subscribable<A>, Resource<A> {

    override val resources: Set<Resource<A>>
        get() = setOf(this)
}
