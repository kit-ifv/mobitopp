@file:Suppress("TooManyFunctions")
package utils.csv

import CodePlan
import Encodable
import ID
import utils.units.CurrencyUnits
import utils.units.DistanceUnit
import utils.units.euros
import utils.units.kilometers
import utils.units.meters
import utils.units.toCurrency
import utils.units.toDistance

fun Row.byte(column: String) = this.invoke(column, String::toByte)
fun Row.short(column: String) = this.invoke(column, String::toShort)
fun Row.int(column: String) = this.invoke(column, String::toInt)
fun Row.long(column: String) = this.invoke(column, String::toLong)
fun Row.float(column: String) = this.invoke(column, String::toFloat)
fun Row.double(column: String) = this.invoke(column, String::toDouble)
fun Row.boolean(column: String) = this.invoke(column, String::toBoolean)
fun <E> Row.id(column: String) = this.invoke(column) { s -> ID<E>(s.toULong()) }
fun <T: Encodable> Row.decode(column: String, codePLan: CodePlan<T>) =
    this.invoke(column) { s ->  codePLan.decode(s.toInt()) }


fun Row.byte() = TypedRow(this, String::toByte)
fun Row.short() = TypedRow(this, String::toShort)
fun Row.int() = TypedRow(this, String::toInt)
fun Row.long() = TypedRow(this, String::toLong)
fun Row.float() = TypedRow(this, String::toFloat)
fun Row.double() = TypedRow(this, String::toDouble)
fun Row.boolean() = TypedRow(this, String::toBoolean)
fun <E> Row.id() = TypedRow(this) { s -> ID<E>(s.toULong()) }
fun <T: Encodable> Row.decode(codePLan: CodePlan<T>) =
    TypedRow(this) { s ->  codePLan.decode(s.toInt()) }


fun <I> I.distance(unit: DistanceUnit) where I: TypedRow<Int> = this.wrap { it.toDistance(unit) }
fun <I> I.distance(column: String, unit: DistanceUnit) where I: TypedRow<Int> =
    this.wrap { it.toDistance(unit) }.invoke(column)
//fun <L> L.distance(unit: DistanceUnit) where L: TypedRow<Long> = this.wrap { it.toDistance(unit) }
//fun <D> D.distance(unit: DistanceUnit) where D: TypedRow<Double> = this.wrap { it.toDistance(unit) }

fun <I> I.meters() where I: TypedRow<Int> = this.wrap { it.meters }
fun <I> I.meters(column: String) where I: TypedRow<Int> = this.wrap { it.meters }.invoke(column)
//fun <L> L.meters() where L: TypedRow<Long> = this.wrap { it.meters }
//fun <D> D.meters() where D: TypedRow<Double> = this.wrap { it.meters }

fun <I> I.kilometers() where I: TypedRow<Int> = this.wrap { it.kilometers }
fun <I> I.kilometers(column: String) where I: TypedRow<Int> = this.wrap { it.kilometers }.invoke(column)
//fun <L> L.kilometers() where L: TypedRow<Long> = this.wrap { it.kilometers }
//fun <D> D.kilometers() where D: TypedRow<Double> = this.wrap { it.kilometers }

fun <I> I.currency(unit: CurrencyUnits) where I: TypedRow<Int> = this.wrap { it.toCurrency(unit) }
fun <I> I.currency(column: String, unit: CurrencyUnits) where I: TypedRow<Int> =
    this.wrap { it.toCurrency(unit) }.invoke(column)
//fun <L> L.currency(unit: CurrencyUnits) where L: TypedRow<Long> = this.wrap { it.toCurrency(unit) }
//fun <D> D.currency(unit: CurrencyUnits) where D: TypedRow<Double> = this.wrap { it.toCurrency(unit) }

fun <I> I.euros() where I: TypedRow<Int> = this.wrap { it.euros }
fun <I> I.euros(column: String) where I: TypedRow<Int> = this.wrap { it.euros }.invoke(column)

