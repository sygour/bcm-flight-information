package fr.sgo.bcm.flightaggregator.information;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InfoRequest {
    private String departureAirport;
    private String arrivalAirport;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private TripType tripType;
}
