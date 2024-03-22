@file:Suppress("TooManyFunctions")
package utils.csv

import CodePlan
import Encodable
import ID
import units.CurrencyUnit
import units.DistanceUnit
import units.euros
import units.kilometers
import units.meters
import units.share
import units.toCurrency
import units.toDistance

fun Row.byte(column: String) = this.invoke(column, String::toByte)
fun Row.short(column: String) = this.invoke(column, String::toShort)
fun Row.int(column: String) = this.invoke(column, String::toInt)
fun Row.long(column: String) = this.invoke(column, String::toLong)
fun Row.float(column: String) = this.invoke(column, String::toFloat)
fun Row.double(column: String) = this.invoke(column, String::toDouble)
fun Row.boolean(column: String) = this.invoke(column, String::toBoolean)
fun <E> Row.id(column: String) = this.invoke(column) { s -> ID<E>(s.toLong()) }
fun <T: Encodable> Row.decode(column: String, codePlan: CodePlan<T>) =
    this.invoke(column) { s ->  codePlan.decode(s.toInt()) }

fun <T: Encodable> Row.decodeName(column: String, codePlan: CodePlan<T>) =
    this.invoke(column) { s ->  codePlan.decode(s) }

fun Row.unitShare(column: String) = this.invoke(column) { s ->  s.toDouble().share() }

fun Row.distance(column: String, unit: DistanceUnit) = this.invoke(column) { it.toDouble().toDistance(unit) }
fun Row.meters(column: String) = this.invoke(column) { it.toDouble().toDistance(DistanceUnit.METERS) }
fun Row.kilometers(column: String) = this.invoke(column) { it.toDouble().toDistance(DistanceUnit.KILOMETERS) }

fun Row.currency(column: String, unit: CurrencyUnit) = this.invoke(column) { it.toDouble().toCurrency(unit) }
fun Row.euros(column: String) = this.invoke(column) { it.toDouble().toCurrency(CurrencyUnit.EUROS) }




fun Row.byte() = TypedRow(this, String::toByte)
fun Row.short() = TypedRow(this, String::toShort)
fun Row.int() = TypedRow(this, String::toInt)
fun Row.long() = TypedRow(this, String::toLong)
fun Row.float() = TypedRow(this, String::toFloat)
fun Row.double() = TypedRow(this, String::toDouble)
fun Row.boolean() = TypedRow(this, String::toBoolean)
fun <E> Row.id() = TypedRow(this) { s -> ID<E>(s.toLong()) }
fun <T: Encodable> Row.decode(codePlan: CodePlan<T>) =
    TypedRow(this) { s ->  codePlan.decode(s.toInt()) }

fun <T: Encodable> Row.decodeName(codePlan: CodePlan<T>) =
    TypedRow(this) { s ->  codePlan.decode(s) }





fun <T> T.distance(unit: DistanceUnit) where T: TypedRow<Int> = this.wrap { it.toDistance(unit) }
fun <T> T.distance(column: String, unit: DistanceUnit) where T: TypedRow<Double> =
    this.wrap { it.toDistance(unit) }.invoke(column)
//fun <L> L.distance(unit: DistanceUnit) where L: TypedRow<Long> = this.wrap { it.toDistance(unit) }
//fun <D> D.distance(unit: DistanceUnit) where D: TypedRow<Double> = this.wrap { it.toDistance(unit) }

fun <T> T.meters() where T: TypedRow<Int> = this.wrap { it.meters }
fun <T> T.meters(column: String) where T: TypedRow<Int> = this.wrap { it.meters }.invoke(column)
//fun <L> L.meters() where L: TypedRow<Long> = this.wrap { it.meters }
//fun <D> D.meters() where D: TypedRow<Double> = this.wrap { it.meters }

fun <T> T.kilometers() where T: TypedRow<Int> = this.wrap { it.kilometers }
fun <T> T.kilometers(column: String) where T: TypedRow<Int> = this.wrap { it.kilometers }.invoke(column)
//fun <L> L.kilometers() where L: TypedRow<Long> = this.wrap { it.kilometers }
//fun <D> D.kilometers() where D: TypedRow<Double> = this.wrap { it.kilometers }

fun <T> T.currency(unit: CurrencyUnit) where T: TypedRow<Int> = this.wrap { it.toCurrency(unit) }
fun <T> T.currency(column: String, unit: CurrencyUnit) where T: TypedRow<Int> =
    this.wrap { it.toCurrency(unit) }.invoke(column)
//fun <L> L.currency(unit: CurrencyUnit) where L: TypedRow<Long> = this.wrap { it.toCurrency(unit) }
//fun <D> D.currency(unit: CurrencyUnit) where D: TypedRow<Double> = this.wrap { it.toCurrency(unit) }

fun <T> T.euros() where T: TypedRow<Int> = this.wrap { it.euros }
fun <T> T.euros(column: String) where T: TypedRow<Int> = this.wrap { it.euros }.invoke(column)

