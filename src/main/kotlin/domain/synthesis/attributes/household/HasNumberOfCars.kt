package domain.synthesis.attributes.household

interface HasNumberOfCars {
    val amountOfCars: Int
}

interface HasMutableNumberOfCars : HasNumberOfCars {
    override var amountOfCars: Int
}
