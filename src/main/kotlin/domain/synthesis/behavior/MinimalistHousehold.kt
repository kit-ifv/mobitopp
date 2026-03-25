package domain.synthesis.behavior

interface MinimalistHousehold<out S, out T> {
    val members: Collection<MinimalistPerson<T>>
    val size get() = members.size
    val attributes: S
}