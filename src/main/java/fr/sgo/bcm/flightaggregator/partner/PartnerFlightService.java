package fr.sgo.bcm.flightaggregator.partner;

import fr.sgo.bcm.flightaggregator.information.model.FlightInformation;

import java.time.LocalDate;
import java.util.stream.Stream;

public interface PartnerFlightService {
    Stream<FlightInformation> call(String departureAirport, String arrivalAirport, LocalDate departureDate);
}
