package modeling.discreteChoice

/**
 * A choice situation may contain additional appended information but is basically only a wrapper for [X]
 * Thus the equals and hash implementaion of that type can be used for mapping to a certain utility function.
 */
abstract class ChoiceSituation<X : Any> {
    abstract val choice: X

    override fun equals(other: Any?): Boolean {
        if (other !is ChoiceSituation<*>) return false
        return choice == other.choice
    }

    override fun hashCode(): Int {
        return choice.hashCode()
    }
}
