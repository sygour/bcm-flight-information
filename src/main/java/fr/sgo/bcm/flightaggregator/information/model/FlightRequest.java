package fr.sgo.bcm.flightaggregator.information.model;

import fr.sgo.bcm.flightaggregator.information.model.TripType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FlightRequest {
    private String departureAirport;
    private String arrivalAirport;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private TripType tripType;
}
