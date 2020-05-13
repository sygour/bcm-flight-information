# bcm-flight-information
BCM Interview project - simulating a flight information aggregator

## Launching the application

### Tests

```
./mvnw test
```

### Run

```
./mvnw spring-boot:run
```

## Implementation description

### API

One endpoint is defined as follow

```
GET     /api/flights?departure_airport=...&arrival_airport=...&departure_date=...&return_date=...&tripType=R|OW
```

It returns a Map where:

- key is the total price
- value is the list of travels matching that price

The tests are quite simple on this class.

### Data / domain description

The `model` package contains the data model of the domain:

- `FlightRequest` contains the request data (API params basically)
- `TravelInformation` is used as return type of the API and contains two flights (the second may be null if trip type is `one way`)

### Aggregation service

This class `AggregationService` contains all the logic of the application and allows to:

- calling all the defined partner services
- combining possible flights for `return` trips
- grouping travels by price

As it is the corner stone of the application, it is quite fully tested.

### Partner package

Here is defined the contract that must be implemented to add an external partner:

- a service must be implemented to return the data to handle

---

## Limitations and enhancements

### Partner services

They are not defined yet, which is a quite big limitation but I focused more on the domain and logic of the application in the given time.

### API

An enhancement would be to prevent overloading the service with to many calls.

### Aggregator service

No caching is done on the service (should maybe be done on the API level)

The data that are returned are redundant: the flights are duplicated. A way to improve and restrain the size of the transferred data would be to change the returned data as follow:

- InfoResponse
  - status
  - flight_request
  - list of flight_data
    - id
    - price
    - departureAirport
    - arrivalAirport
    - departureTime
    - arrivalTime
  - travels_by_price
    - price
    - list of travel
      - forward_flight_id
      - return_flight_id

That way, we send only once information about flight regardless of the possible travels.

### Tech improvements

- CI could be configured (on Travis for example)
- The app could be dockerized to be deployed
