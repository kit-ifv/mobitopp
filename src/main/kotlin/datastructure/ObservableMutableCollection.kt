//package datastructure
//
//fun interface Listener<E> {
//    fun update(new: E)
//}
//
//class ObservableList<E>(private val collection: MutableList<E& Any>): MutableList<E& Any> by collection {
//    val listener: MutableList<Listener<List<E>>>  = mutableListOf()
//    override fun add(element: E & Any): Boolean {
//        val added = collection.add(element)
//        if(added){
//            listener.forEach { it.update(collection) }
//        }
//        return added
//    }
//
//    override fun remove(element: E& Any): Boolean {
//        val removed = collection.remove(element)
//        if(removed) {
//            listener.forEach { it.update(collection) }
//
//        }
//        return removed
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if(other !is Collection<*>) return false
//        if(other.size != size) return false
//        return other.zip(collection).all { (a, b) -> b == a }
//    }
//}
//fun <T> observableListOf(): ObservableList<T & Any> = ObservableList(ArrayList())
