package fr.sgo.bcm.flightaggregator.information;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class InfoRequestApi {

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
        return ResponseEntity.ok(InfoResponse.builder()
                .status(HttpStatus.OK)
                .request(InfoRequest.builder()
                        .departureAirport(departureAirport)
                        .arrivalAirport(arrivalAirport)
                        .departureDate(departureDate)
                        .returnDate(returnDate)
                        .tripType(tripType)
                        .build())
                .build());
    }
}
