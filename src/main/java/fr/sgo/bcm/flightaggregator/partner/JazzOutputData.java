package fr.sgo.bcm.flightaggregator.partner;

import fr.sgo.bcm.flightaggregator.information.model.FlightInformation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JazzOutputData implements Serializable {
    private Float price;
    private JazzFlight flight;

    @Data
    @NoArgsConstructor
    private static class JazzFlight implements Serializable {
        private UUID id;
        private String departure_airport;
        private String arrival_airport;
        private LocalDateTime departure_time;
        private LocalDateTime arrival_time;
    }

    public FlightInformation convert() {
        return FlightInformation.builder()
                .price(getPrice())
                .id(getFlight().getId())
                .departureAirport(getFlight().getDeparture_airport())
                .arrivalAirport(getFlight().getArrival_airport())
                .departureTime(getFlight().getDeparture_time())
                .arrivalTime(getFlight().getArrival_time())
                .build();
    }
}
