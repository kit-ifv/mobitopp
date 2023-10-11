package application.usecases

import application.synthesis.IdRepository
import domain.region.ZoneData

class DefaultContext {
    lateinit var zoneRepo: IdRepository<ZoneData>

}