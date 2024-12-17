import domain.data.Person
import domain.data.Zone
import domain.location.Metrics
import units.CurrencyUnit
import units.NumericUnit
import usecases.LegacyMode
import utils.units.AbsoluteTime
import kotlin.math.ln
import kotlin.math.pow

//Data types for nest structure
interface Structure<T>{
    val name: String
    val options: List<Option<T>>
}

data class Nest<T, P>(
    override val name: String,
//    val lambda: Double,
): Structure<T> {
    private val children = mutableListOf<Structure<T>>()

    private lateinit var lambdaSelector: (P.() -> Double)

    fun lambda(parameters: P) = require(this::lambdaSelector.isInitialized) {
        "error, lambda not set for nest '$name'!\n" +
                "Consider adding '.withLambda{ 1.0 //TODO }' as suffix to the nest definition."
    }.let {
        lambdaSelector(parameters)
    }

    fun nest(name: String, scope: Nest<T, P>.() -> Unit): Nest<T, P> { //lambda: Double, //TODO check availability
        val newChild = Nest<T, P>(name) //lambda
        newChild.scope()
        children.add(newChild)
        return this
    }

    fun option(name: String, value: T) {
        children.add(Option(name, value))
    }

    override val options: List<Option<T>>
        get() = children.flatMap { it.options }

    fun withLambda(lambda: P.() -> Double): Nest<T, P> {
        lambdaSelector = lambda
        return this
    }
}


data class Option<T>(
    override val name: String,
    val value: T,
): Structure<T> {
    override val options = listOf(this)
}



//overall choice model holding nest structure and utility functions (terms)
typealias Term<S> = (S) -> Double

fun <T,X, P, S> choicemodel(name: String, options: Nest<T, P>.() -> Unit): ChoiceModelTest<T, P, S> {
    val structure = Nest<T, P>("root").withLambda { 1.0 }.apply(options)
    return ChoiceModelTest<T, P, S>(name, structure)
}


data class ChoiceModelTest<T, P, S>(
    val name: String,
    val structure: Structure<T>
) {
    private val optionsByName = structure.options.associateBy { it.name }
    val utilityFunctions = mutableMapOf<String, P.() -> Term<S>>()

    fun utilities(scope: UtilityCollector<P, S>.() -> Unit): ChoiceModelTest<T, P, S> {
        val collector = UtilityCollector<P, S>(optionsByName.keys.toList())
        collector.scope()
        this.utilityFunctions.putAll(collector.utilityFunctions)
        return this
    }

}



//scope functions and operators to build up utility functions

//class Property<S>(val compute: S.() -> Number) {
//    inline fun evaluate(situation: S): Double = compute(situation).toDouble()
//
//    //Property X Double -> Term
//    inline operator fun plus(value: Double): Term<S> = { evaluate(it) + value }
//    inline operator fun times(value: Double): Term<S> = { evaluate(it) * value }
//    inline operator fun minus(value: Double): Term<S> = { evaluate(it) - value }
//    inline operator fun div(value: Double): Term<S> = { evaluate(it) / value }
//
//    //Property X Property -> Term
//    inline operator fun plus(property: Property<S>): Term<S> = { evaluate(it) + property.evaluate(it) }
//    inline operator fun times(property: Property<S>): Term<S> = { evaluate(it) * property.evaluate(it) }
//    inline operator fun minus(property: Property<S>): Term<S> = { evaluate(it) - property.evaluate(it) }
//    inline operator fun div(property: Property<S>): Term<S> = { evaluate(it) / property.evaluate(it) }
//
//    //Property X Term -> Term
//    inline operator fun plus(crossinline term: Term<S>): Term<S> = { evaluate(it) + term(it) }
//    inline operator fun times(crossinline term: Term<S>): Term<S> = { evaluate(it) * term(it) }
//    inline operator fun minus(crossinline term: Term<S>): Term<S> = { evaluate(it) - term(it) }
//    inline operator fun div(crossinline term: Term<S>): Term<S> = { evaluate(it) / term(it) }
//}

inline fun <S> property(crossinline compute: S.() -> Number): Term<S> = { compute(it).toDouble() }


//(Double X Double)
//Double X Property -> Term
//inline operator fun <S> Double.plus(property: Property<S>): Term<S> = { situation -> this@plus + property.evaluate(situation) }
//inline operator fun <S> Double.times(property: Property<S>): Term<S> = { situation -> this@times * property.evaluate(situation) }
//inline operator fun <S> Double.minus(property: Property<S>): Term<S> = { situation -> this@minus - property.evaluate(situation) }
//inline operator fun <S> Double.div(property: Property<S>): Term<S> = { situation -> this@div / property.evaluate(situation) }

//Double X Term -> Term
inline operator fun <S> Double.plus(crossinline term: Term<S>): Term<S> = { situation -> this@plus + term(situation) }
inline operator fun <S> Double.times(crossinline term: Term<S>): Term<S> = { situation -> this@times * term(situation) }
inline operator fun <S> Double.minus(crossinline term: Term<S>): Term<S> = { situation -> this@minus - term(situation) }
inline operator fun <S> Double.div(crossinline term: Term<S>): Term<S> = { situation -> this@div / term(situation) }
inline fun <S> Double.toPowerOf(crossinline term: Term<S>): Term<S> = { situation -> this@toPowerOf.pow(term(situation)) }


inline fun <S> log(crossinline term: Term<S>): Term<S> = { situation -> ln(term(situation)) }
inline fun <S> exp(crossinline term: Term<S>): Term<S> = { situation -> kotlin.math.exp(term(situation)) }

// Term X Double -> Term
inline operator fun <S> Term<S>.plus(value: Double): Term<S> = { situation -> this@plus(situation) + value }
inline operator fun <S> Term<S>.times(value: Double): Term<S> = { situation -> this@times(situation) * value }
inline operator fun <S> Term<S>.minus(value: Double): Term<S> = { situation -> this@minus(situation) - value }
inline operator fun <S> Term<S>.div(value: Double): Term<S> = { situation -> this@div(situation) / value }
inline fun <S> Term<S>.toPowerOf(value: Double): Term<S> = { situation -> this@toPowerOf(situation).pow(value) }

//Term X Property -> Term
//inline operator fun <S> Term<S>.plus(property: Property<S>): Term<S> = { situation -> this@plus(situation) + property.evaluate(situation) }
//inline operator fun <S> Term<S>.times(property: Property<S>): Term<S> = { situation -> this@times(situation) * property.evaluate(situation) }
//inline operator fun <S> Term<S>.minus(property: Property<S>): Term<S> = { situation -> this@minus(situation) - property.evaluate(situation) }
//inline operator fun <S> Term<S>.div(property: Property<S>): Term<S> = { situation -> this@div(situation) / property.evaluate(situation) }

//Term X Term -> Term
inline operator fun <S> Term<S>.plus(crossinline term: Term<S>): Term<S> = { situation -> this@plus(situation) + term(situation) }
inline operator fun <S> Term<S>.times(crossinline term: Term<S>): Term<S> ={ situation -> this@times(situation) * term(situation) }
inline operator fun <S> Term<S>.minus(crossinline term: Term<S>): Term<S> = { situation -> this@minus(situation) - term(situation) }
inline operator fun <S> Term<S>.div(crossinline term: Term<S>): Term<S> = { situation -> this@div(situation) / term(situation) }
inline fun <S> Term<S>.toPowerOf(crossinline term: Term<S>): Term<S> = { situation -> this@toPowerOf(situation).pow(term(situation)) }


class UtilityCollector<P, S>(
    private val expectedOptions: List<String>,
) {
    val utilityFunctions = mutableMapOf<String, P.() -> Term<S>>()

    fun <X: P> utility(name: String, scope: X.() -> Term<S>) {
        require(name in expectedOptions) {
            "$name is not a valid option in the defined model. Expected one of: $expectedOptions"
        }
//        utilityFunctions[name] = scope
    }
//    fun <X: P> utility(name: String, papa: X, scope: X.() -> Double) {
//        require(name in expectedOptions) {
//            "$name is not a valid option in the defined model. Expected one of: $expectedOptions"
//        }
////        utilityFunctions[name] = scope
//    }

}





interface Situation<C> {
    val choice: C
}



// --------------- user input area ---------------------------------

//@ChoiceSituation -> generate function alias for property and choice model definition, default situations can be predefined
data class ModeChoiceSituation(
    override val choice: LegacyMode,
    val time: AbsoluteTime,
    val person: Person,
    val origin: Zone,
    val destination: Zone,
    val impedance: Metrics,
): Situation<LegacyMode>
//autogenerate through annotation?
inline fun prop(crossinline compute: ModeChoiceSituation.() -> Number) = property<ModeChoiceSituation>(compute)
fun <X> modeChoiceModel(name: String,  options: Nest<LegacyMode, Papa>.() -> Unit) =
    choicemodel<LegacyMode, X, Papa, ModeChoiceSituation>(name, options)



//User defines parameters
data class ParametersRaExmpl(
    val b_tt: Double,
    val asc_ped: Double,
    val asc_car_d: Double,
    val lambda_car: Double,
    val lambda_iv: Double,
    val lambda_put: Double,
)

//Less boilerplate for helpers, defaults could be predefined in mobitopp framework code
private val AGE = prop { person.age }
val DISTANCE = prop {
    impedance.distance(origin.centroid, destination.centroid, choice).inWholeMeters
}
private val TRAVEL_TIME = prop {
    impedance.duration(origin.centroid, destination.centroid, choice, time).inWholeMinutes
}
val ATTRACTIVITY = prop {
    person.income.toInt(CurrencyUnit.EUROS)
}
val PARKDRUCK = prop { 100 }
val NUMBER_OF_CARS = prop{person.household.cars.count()}
//in some other parameter file
val parameterSet1 = ParametersRaExmpl(
    b_tt = 1.42,
    asc_ped = 3.1415,
    asc_car_d = -1.337,
    lambda_car = 0.3,
    lambda_iv = 0.4,
    lambda_put = 0.3,
)

val parameterSet2 = ParametersRaExmpl(
    b_tt = 1.5,
    asc_ped = 3.2,
    asc_car_d = -1.4,
    lambda_car = 0.2,
    lambda_iv = 0.2,
    lambda_put = 0.6,
)

data class PedParam(
    val asc: Double
): Papa
data class CarParams(
    val asc: Double,
    val runtime: Double
): Papa

interface Papa
data class PSet(
    val lambda: Lambdas,
    val ped: PedParam
)
data class Lambdas(
    val lambda1:Double = 1.0
)
val param1 = PedParam(1.0)
val param2 = CarParams(2.0, 1.0)
val choiceModel = modeChoiceModel<PSet>("LegacyModeChoice") {

    nest("IV") {
        option("walk", LegacyMode.PEDESTRIAN)
        option("bike", LegacyMode.BIKE)
        nest("car") {
            option("car_d", LegacyMode.CAR)
            option("car_p", LegacyMode.PASSENGER)
        }.withLambda { 1.0 }

    }.withLambda { 1.0 }

    nest("PUT") {
        option("put", LegacyMode.PUBLICTRANSPORT)
        option("taxi", LegacyMode.TAXI)
    }.withLambda { 1.0 }

}.utilities {
//
//    utility<PedParam>("walk") {
//        asc + TRAVEL_TIME
//        1.0
////        asc + TRAVEL_TIME  + 1.0
////        asc_ped + b_tt * TRAVEL_TIME
//    }
//
//    utility<CarParams>("car") {
//
//        asc * NUMBER_OF_CARS + asc * partition {
//            AGE
//        }
////        b_tt * NUMBER_OF_CARS +
////        asc_ped +
////        asc_car_d +
////                b_tt * TRAVEL_TIME +
////                log(ATTRACTIVITY) +
////                exp(b_tt*AGE) +
////                PARKDRUCK.toPowerOf(b_tt * AGE) +
////                asc_ped.toPowerOf(AGE)
//    }

}


// ------------------------------------------------

//internally use factory to create choice situation / add the choice option to the situation
//here its LegacyMode:
val baseSituation: ModeChoiceSituation = TODO()
val situationOf: (LegacyMode) -> ModeChoiceSituation = { baseSituation.copy(choice = it) }

//internally evaluate utility function via:
fun foo(sit: ModeChoiceSituation) {

    val expa = PedParam(1.0)
    val capa = CarParams(1.0, 1.20)
    val res = choiceModel.utilityFunctions["walk"]!!.invoke(expa).invoke(sit)
}