package fr.sgo.bcm.flightaggregator.information.api;

import fr.sgo.bcm.flightaggregator.information.model.FlightRequest;
import fr.sgo.bcm.flightaggregator.information.model.TravelInformation;
import fr.sgo.bcm.flightaggregator.information.model.TripType;
import fr.sgo.bcm.flightaggregator.information.service.AggregatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InfoRequestApi {

    private final AggregatorService aggregatorService;

    @Autowired
    public InfoRequestApi(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    /**
     * GET /api/flights?departure_airport=...&arrival_airport=...&departure_date=...&return_date=...&tripType=R|OW
     * http://localhost:8080/api/flights?departure_airport=CDG&arrival_airport=LHR&departure_date=2020-12-15&return_date=2020-12-25&tripType=R
     * http://localhost:8080/api/flights?departure_airport=CDG&arrival_airport=LHR&departure_date=2020-12-15&tripType=OW
     *
     * @return
     */
    @GetMapping("/flights")
    public ResponseEntity<InfoResponse> getFlightInformation(
            @RequestParam("departure_airport") String departureAirport,
            @RequestParam("arrival_airport") String arrivalAirport,
            @RequestParam("departure_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(value = "return_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam("tripType") TripType tripType
    ) {
        if (TripType.R.equals(tripType) && returnDate == null) {
            return ResponseEntity.badRequest().body(null);
        }
        final FlightRequest flightRequest = FlightRequest.builder()
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .departureDate(departureDate)
                .returnDate(returnDate)
                .tripType(tripType)
                .build();

        final Map<Float, List<TravelInformation>> aggregation = aggregatorService.listFlightInformation(flightRequest);

        return ResponseEntity.ok(InfoResponse.builder()
                .status(HttpStatus.OK)
                .request(flightRequest)
                .travelsByPrice(aggregation)
                .build());
    }
}
