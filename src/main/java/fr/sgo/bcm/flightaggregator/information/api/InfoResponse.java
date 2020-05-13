package fr.sgo.bcm.flightaggregator.information.api;

import fr.sgo.bcm.flightaggregator.information.model.FlightRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class InfoResponse {
    private HttpStatus status;
    private FlightRequest request;
}
