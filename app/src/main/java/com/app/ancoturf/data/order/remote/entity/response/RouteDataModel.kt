data class RouteDataModel(
    val routes: List<Route>,
    val status: String
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String,
    val end_location: EndLocation,
    val start_address: String,
    val start_location: StartLocation
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class EndLocation(
    val lat: Double,
    val lng: Double
)

data class StartLocation(
    val lat: Double,
    val lng: Double
)