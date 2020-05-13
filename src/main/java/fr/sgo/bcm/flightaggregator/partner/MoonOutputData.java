package fr.sgo.bcm.flightaggregator.partner;

import fr.sgo.bcm.flightaggregator.information.model.FlightInformation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MoonOutputData implements Serializable {
    private Float price;
    private List<MoonFlight> legs;

    @Data
    @NoArgsConstructor
    private static class MoonFlight implements Serializable {
        private UUID id;
        private String departure_airport;
        private String arrival_airport;
        private LocalDateTime departure_time;
        private LocalDateTime arrival_time;
    }

    public FlightInformation convert() {
        final MoonFlight flight = getLegs().get(0);
        FlightInformation.FlightInformationBuilder builder = FlightInformation.builder()
                .price(getPrice());
        if (flight != null) {
            builder
                    .id(flight.getId())
                    .departureAirport(flight.getDeparture_airport())
                    .arrivalAirport(flight.getArrival_airport())
                    .departureTime(flight.getDeparture_time())
                    .arrivalTime(flight.getArrival_time());
        }
        return builder.build();
    }
}
