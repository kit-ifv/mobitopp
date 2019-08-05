package edu.kit.ifv.mobitopp.visum;

public abstract class StandardNetfileLanguages {

  public static NetfileLanguage english() {
    DynamicNetfileLanguage language = new DynamicNetfileLanguage();
    language.add(Table.transportSystems, "TSYS");
    language.add(Table.linkTypes, "LINKTYPE");
    language.add(Table.nodes, "NODE");
    language.add(Table.links, "LINK");
    language.add(Table.turns, "TURN");
    language.add(Table.zones, "ZONE");
    language.add(Table.surface, "SURFACEITEM");
    language.add(Table.point, "POINT");
    language.add(Table.intermediatePoint, "EDGEITEM");
    language.add(Table.edges, "EDGE");
    language.add(Table.faces, "FACEITEM");
    language.add(Table.territories, "TERRITORY");
    language.add(Table.poiCategory, "POICATEGORY");
    language.add(Table.poiCategoryPrefix, "POIOFCAT_");
    // TODO translation still missing
    language.add(Table.connectors, "ANBINDUNG");
    language.add(Table.vehicleUnit, "FZGEINHEIT");
    language.add(Table.vehicleCombinations, "FZGKOMB");
    language.add(Table.vehicleUnitToCombinations, "FZGEINHEITZUFZGKOMB");
    language.add(Table.station, "HALTESTELLE");
    language.add(Table.stopArea, "HALTESTELLENBEREICH");
    language.add(Table.stop, "HALTEPUNKT");
    language.add(Table.transferWalkTimes, "UEBERGANGSGEHZEITHSTBER");
    language.add(Table.line, "LINIE");
    language.add(Table.lineRoute, "LINIENROUTE");
    language.add(Table.lineRouteElement, "LINIENROUTENELEMENT");
    language.add(Table.timeProfile, "FAHRZEITPROFIL");
    language.add(Table.timeProfileElement, "FAHRZEITPROFILELEMENT");
    language.add(Table.vehicleJourney, "FAHRPLANFAHRT");
    language.add(Table.vehicleJourneyPart, "FAHRPLANFAHRTABSCHNITT");
    
    // TransportSystem
    language.add(StandardAttributes.code, "CODE");
    language.add(StandardAttributes.name, "NAME");
    language.add(StandardAttributes.type, "TYPE");
    
    // LinkType
    language.add(StandardAttributes.number, "NO");
    language.add(StandardAttributes.transportSystemSet, "TSYSSET");
    language.add(StandardAttributes.numberOfLanes, "NUMLANES");
    language.add(StandardAttributes.capacityCar, "CAPPRT");
    language.add(StandardAttributes.freeFlowSpeedCar, "V0PRT");

    // Node
    language.add(StandardAttributes.typeNumber, "TYPENO");
    language.add(StandardAttributes.xCoord, "XCOORD");
    language.add(StandardAttributes.yCoord, "YCOORD");
    language.add(StandardAttributes.zCoord, "ZCOORD");
    
    // Link
    language.add(StandardAttributes.fromNodeNumber, "FROMNODENO");
    language.add(StandardAttributes.toNodeNumber, "TONODENO");
    language.add(StandardAttributes.length, "LENGTH");
    language.add(StandardAttributes.individualWalkSpeed, "VMAX-IVSYS(FUSS)");
    language.add(StandardAttributes.publicTransportWalkSpeed, "VSTD-OEVSYS(F)");
    
    // Abbieger
    language.add(StandardAttributes.freeFlowTravelTimeCar, "T0PRT");
    language.add(StandardAttributes.viaNodeNumber, "VIANODENO");
    
    // Zone
    language.add(StandardAttributes.parkingPlaces, "PARKRAUM");
    language.add(StandardAttributes.mainZoneNumber, "MAINZONENO");
    language.add(StandardAttributes.areaId, "SURFACEID");
    language.add(StandardAttributes.chargingStations, "LADESTATIONEN");
    language.add(StandardAttributes.car2GoTerritory, "CAR2GO_GEBIET");
    language.add(StandardAttributes.car2GoStartState, "CAR2GO_AUSGANGSZUSTAND");
    language.add(StandardAttributes.carSharingDensityCar2Go, "FZ_FL_C2G");
    language.add(StandardAttributes.carSharingDensityFlinkster, "FZ_FL_FL");
    language.add(StandardAttributes.carSharingDensityStadtmobil, "FZ_FL_SM");
    language.add(StandardAttributes.privateChargingProbability, "ANTEIL_STE");
    language.add(StandardAttributes.innerZonePublicTransportTravelTime, "DIAG_OEV");
    
    // Connector
    language.add(StandardAttributes.zoneNumber, "ZONENO");
    language.add(StandardAttributes.nodeNumber, "NODENO");
    language.add(StandardAttributes.direction, "DIRECTION");
    language.add(StandardAttributes.travelTimeCar, "T0-VSYS(P)");
    
    // VehicleUnit
    language.add(StandardAttributes.vehicleCapacity, "GESAMTPL");
    language.add(StandardAttributes.seats, "SITZPL");
    
    // VehicleCombination
    language.add(StandardAttributes.vehicleCombinationNumber, "FZGKOMBNR");
    language.add(StandardAttributes.vehicleUnitNumber, "FZGEINHEITNR");
    language.add(StandardAttributes.numberOfVehicleUnits, "ANZFZGEINH");

    // StopArea
    language.add(StandardAttributes.stationNumber, "HSTNR");
    
    // Stop
    language.add(StandardAttributes.stopAreaNumber, "HSTBERNR");
    language.add(StandardAttributes.directed, "GERICHTET");
    language.add(StandardAttributes.linkNumber, "STRNR");
    language.add(StandardAttributes.relativePosition, "RELPOS");
    
    // TransferWalkTimes
    language.add(StandardAttributes.fromStopArea, "VONHSTBERNR");
    language.add(StandardAttributes.toStopArea, "NACHHSTBERNR");
    language.add(StandardAttributes.time, "ZEIT");
    language.add(StandardAttributes.transportSystemCode, "VSYSCODE");
    
    // Pt LineRoute
    language.add(StandardAttributes.lineName, "LINNAME");
    language.add(StandardAttributes.directionCode, "RICHTUNGCODE");
    
    // Pt LineRouteElement
    language.add(StandardAttributes.stopNumber, "HPUNKTNR");
    language.add(StandardAttributes.index, "INDEX");
    language.add(StandardAttributes.isRoutePoint, "ISTROUTENPUNKT");
    language.add(StandardAttributes.toLength, "NACHLAENGE");
    
    // TimeProfile
    language.add(StandardAttributes.lineRouteName, "LINROUTENAME");
    language.add(StandardAttributes.timeProfileName, "FZPROFILNAME");
    language.add(StandardAttributes.lineRouteElementIndex, "LRELEMINDEX");
    language.add(StandardAttributes.getOff, "AUS");
    language.add(StandardAttributes.board, "EIN");
    language.add(StandardAttributes.arrival, "ANKUNFT");
    language.add(StandardAttributes.departure, "ABFAHRT");
    
    // VehicleJourney
    language.add(StandardAttributes.vehicleJourneyNumber, "FPLFAHRTNR");
    language.add(StandardAttributes.fromTimeProfileElementIndex, "VONFZPELEMINDEX");
    language.add(StandardAttributes.toTimeProfileElementIndex, "NACHFZPELEMINDEX");
    language.add(StandardAttributes.vehicleDayNumber, "VTAGNR");
    
    // Point
    language.add(StandardAttributes.id, "ID");
    
    // IntermediatePoint
    language.add(StandardAttributes.edgeId, "EDGEID");
    
    // Edge
    language.add(StandardAttributes.fromPointId, "FROMPOINTID");
    language.add(StandardAttributes.toPointId, "TOPOINTID");
    
    // Surfae
    language.add(StandardAttributes.enclave, "ENCLAVE");
    language.add(StandardAttributes.ringId, "FACEID");
    
    // ChargingStation
    language.add(StandardAttributes.chargingStationsCode, "Ladestationen");
    language.add(StandardAttributes.chargingType, "ART");
    language.add(StandardAttributes.lsId, "LS_ID");
    language.add(StandardAttributes.latitude, "LATITUDE");
    language.add(StandardAttributes.longitude, "LONGITUDE");
    language.add(StandardAttributes.vehicleType, "FZ");
    language.add(StandardAttributes.publicType, "PUB");
    language.add(StandardAttributes.place, "ORT");
    language.add(StandardAttributes.plz, "PLZ");
    language.add(StandardAttributes.street, "STRASSE");
    
    // ChargingPoints
    language.add(StandardAttributes.chargingPoints, "Ladepunkte");
    language.add(StandardAttributes.power, "LEISTUNG");
    
    // Carsharing
    language.add(StandardAttributes.carsharingStadtmobil, "CS_SM");
    language.add(StandardAttributes.carsharingFlinkster, "CS_FL");
    language.add(StandardAttributes.objectId, "OBJECTID");
    language.add(StandardAttributes.numberOfVehicles, "ANZAHL_FZ");
    language.add(StandardAttributes.town, "STADT");
    language.add(StandardAttributes.streetIso8859, "STRA\u00DFE");
    
    // Territory
    language.add(StandardAttributes.item, "ITEM");
    language.add(StandardAttributes.codeLc, "NAME");

    // Units
    language.add(Unit.velocity, "km/h");
    language.add(Unit.distance, "km");
    language.add(Unit.time, "s");
    return language;
  }

  public static NetfileLanguage german() {
    DynamicNetfileLanguage language = new DynamicNetfileLanguage();
    language.add(Table.transportSystems, "VSYS");
    language.add(Table.linkTypes, "STRECKENTYP");
    language.add(Table.nodes, "KNOTEN");
    language.add(Table.links, "STRECKE");
    language.add(Table.turns, "ABBIEGER");
    language.add(Table.zones, "BEZIRK");
    language.add(Table.connectors, "ANBINDUNG");
    language.add(Table.vehicleUnit, "FZGEINHEIT");
    language.add(Table.vehicleCombinations, "FZGKOMB");
    language.add(Table.vehicleUnitToCombinations, "FZGEINHEITZUFZGKOMB");
    language.add(Table.station, "HALTESTELLE");
    language.add(Table.stopArea, "HALTESTELLENBEREICH");
    language.add(Table.stop, "HALTEPUNKT");
    language.add(Table.transferWalkTimes, "UEBERGANGSGEHZEITHSTBER");
    language.add(Table.line, "LINIE");
    language.add(Table.lineRoute, "LINIENROUTE");
    language.add(Table.lineRouteElement, "LINIENROUTENELEMENT");
    language.add(Table.timeProfile, "FAHRZEITPROFIL");
    language.add(Table.timeProfileElement, "FAHRZEITPROFILELEMENT");
    language.add(Table.vehicleJourney, "FAHRPLANFAHRT");
    language.add(Table.vehicleJourneyPart, "FAHRPLANFAHRTABSCHNITT");
    language.add(Table.surface, "FLAECHENELEMENT");
    language.add(Table.point, "PUNKT");
    language.add(Table.intermediatePoint, "ZWISCHENPUNKT");
    language.add(Table.edges, "KANTE");
    language.add(Table.faces, "TEILFLAECHENELEMENT");
    language.add(Table.territories, "GEBIET");
    language.add(Table.poiCategory, "POIKATEGORIE");
    language.add(Table.poiCategoryPrefix, "POIOFCAT_");

    // TransportSystem
    language.add(StandardAttributes.code, "CODE");
    language.add(StandardAttributes.name, "NAME");
    language.add(StandardAttributes.type, "TYP");
    
    // LinkType
    language.add(StandardAttributes.number, "NR");
    language.add(StandardAttributes.transportSystemSet, "VSYSSET");
    language.add(StandardAttributes.numberOfLanes, "ANZFAHRSTREIFEN");
    language.add(StandardAttributes.capacityCar, "KAPIV");
    language.add(StandardAttributes.freeFlowSpeedCar, "V0IV");

    // Node
    language.add(StandardAttributes.typeNumber, "TYPNR");
    language.add(StandardAttributes.xCoord, "XKOORD");
    language.add(StandardAttributes.yCoord, "YKOORD");
    language.add(StandardAttributes.zCoord, "ZKOORD");
    
    // Link
    language.add(StandardAttributes.fromNodeNumber, "VONKNOTNR");
    language.add(StandardAttributes.toNodeNumber, "NACHKNOTNR");
    language.add(StandardAttributes.length, "LAENGE");
    language.add(StandardAttributes.individualWalkSpeed, "VMAX-IVSYS(FUSS)");
    language.add(StandardAttributes.publicTransportWalkSpeed, "VSTD-OEVSYS(F)");
    
    // Abbieger
    language.add(StandardAttributes.freeFlowTravelTimeCar, "T0IV");
    language.add(StandardAttributes.viaNodeNumber, "UEBERKNOTNR");
    
    // Zone
    language.add(StandardAttributes.parkingPlaces, "PARKRAUM");
    language.add(StandardAttributes.mainZoneNumber, "OBEZNR");
    language.add(StandardAttributes.areaId, "FLAECHEID");
    language.add(StandardAttributes.chargingStations, "LADESTATIONEN");
    language.add(StandardAttributes.car2GoTerritory, "CAR2GO_GEBIET");
    language.add(StandardAttributes.car2GoStartState, "CAR2GO_AUSGANGSZUSTAND");
    language.add(StandardAttributes.carSharingDensityCar2Go, "FZ_FL_C2G");
    language.add(StandardAttributes.carSharingDensityFlinkster, "FZ_FL_FL");
    language.add(StandardAttributes.carSharingDensityStadtmobil, "FZ_FL_SM");
    language.add(StandardAttributes.privateChargingProbability, "ANTEIL_STE");
    language.add(StandardAttributes.innerZonePublicTransportTravelTime, "DIAG_OEV");
    
    // Connector
    language.add(StandardAttributes.zoneNumber, "BEZNR");
    language.add(StandardAttributes.nodeNumber, "KNOTNR");
    language.add(StandardAttributes.direction, "RICHTUNG");
    language.add(StandardAttributes.travelTimeCar, "T0-VSYS(P)");
    
    // VehicleUnit
    language.add(StandardAttributes.vehicleCapacity, "GESAMTPL");
    language.add(StandardAttributes.seats, "SITZPL");
    
    // VehicleCombination
    language.add(StandardAttributes.vehicleCombinationNumber, "FZGKOMBNR");
    language.add(StandardAttributes.vehicleUnitNumber, "FZGEINHEITNR");
    language.add(StandardAttributes.numberOfVehicleUnits, "ANZFZGEINH");

    // StopArea
    language.add(StandardAttributes.stationNumber, "HSTNR");
    
    // Stop
    language.add(StandardAttributes.stopAreaNumber, "HSTBERNR");
    language.add(StandardAttributes.directed, "GERICHTET");
    language.add(StandardAttributes.linkNumber, "STRNR");
    language.add(StandardAttributes.relativePosition, "RELPOS");
    
    // TransferWalkTimes
    language.add(StandardAttributes.fromStopArea, "VONHSTBERNR");
    language.add(StandardAttributes.toStopArea, "NACHHSTBERNR");
    language.add(StandardAttributes.time, "ZEIT");
    language.add(StandardAttributes.transportSystemCode, "VSYSCODE");
    
    // Pt LineRoute
    language.add(StandardAttributes.lineName, "LINNAME");
    language.add(StandardAttributes.directionCode, "RICHTUNGCODE");
    
    // Pt LineRouteElement
    language.add(StandardAttributes.stopNumber, "HPUNKTNR");
    language.add(StandardAttributes.index, "INDEX");
    language.add(StandardAttributes.isRoutePoint, "ISTROUTENPUNKT");
    language.add(StandardAttributes.toLength, "NACHLAENGE");
    
    // TimeProfile
    language.add(StandardAttributes.lineRouteName, "LINROUTENAME");
    language.add(StandardAttributes.timeProfileName, "FZPROFILNAME");
    language.add(StandardAttributes.lineRouteElementIndex, "LRELEMINDEX");
    language.add(StandardAttributes.getOff, "AUS");
    language.add(StandardAttributes.board, "EIN");
    language.add(StandardAttributes.arrival, "ANKUNFT");
    language.add(StandardAttributes.departure, "ABFAHRT");
    
    // VehicleJourney
    language.add(StandardAttributes.vehicleJourneyNumber, "FPLFAHRTNR");
    language.add(StandardAttributes.fromTimeProfileElementIndex, "VONFZPELEMINDEX");
    language.add(StandardAttributes.toTimeProfileElementIndex, "NACHFZPELEMINDEX");
    language.add(StandardAttributes.vehicleDayNumber, "VTAGNR");
    
    // Point
    language.add(StandardAttributes.id, "ID");
    
    // IntermediatePoint
    language.add(StandardAttributes.edgeId, "KANTEID");
    
    // Edge
    language.add(StandardAttributes.fromPointId, "VONPUNKTID");
    language.add(StandardAttributes.toPointId, "NACHPUNKTID");
    
    // Surfae
    language.add(StandardAttributes.enclave, "ENKLAVE");
    language.add(StandardAttributes.ringId, "TFLAECHEID");
    
    // ChargingStation
    language.add(StandardAttributes.chargingStationsCode, "Ladestationen");
    language.add(StandardAttributes.chargingType, "ART");
    language.add(StandardAttributes.lsId, "LS_ID");
    language.add(StandardAttributes.latitude, "LATITUDE");
    language.add(StandardAttributes.longitude, "LONGITUDE");
    language.add(StandardAttributes.vehicleType, "FZ");
    language.add(StandardAttributes.publicType, "PUB");
    language.add(StandardAttributes.place, "ORT");
    language.add(StandardAttributes.plz, "PLZ");
    language.add(StandardAttributes.street, "STRASSE");
    
    // ChargingPoints
    language.add(StandardAttributes.chargingPoints, "Ladepunkte");
    language.add(StandardAttributes.power, "LEISTUNG");
    
    // Carsharing
    language.add(StandardAttributes.carsharingStadtmobil, "CS_SM");
    language.add(StandardAttributes.carsharingFlinkster, "CS_FL");
    language.add(StandardAttributes.objectId, "OBJECTID");
    language.add(StandardAttributes.numberOfVehicles, "ANZAHL_FZ");
    language.add(StandardAttributes.town, "STADT");
    language.add(StandardAttributes.streetIso8859, "STRA\u00DFE");
    
    // Territory
    language.add(StandardAttributes.item, "ITEM");
    language.add(StandardAttributes.codeLc, "CODE_LC");
    
    language.add(Unit.velocity, "km/h");
    language.add(Unit.distance, "km");
    language.add(Unit.time, "s");
    return language;
  }
}
