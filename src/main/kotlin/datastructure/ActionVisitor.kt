package datastructure

interface ActionVisitor<T> {
    fun visitLeg(leg: Leg): T
    fun visitActivity(activity: Activity): T
}

interface ActionBlockVisitor<T> {
    fun visitTrip(leg: LinkTrip): T
    fun visitActivityBlock(activity: Agenda): T
}
