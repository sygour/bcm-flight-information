package fr.sgo.bcm.flightaggregator.information.api;

import fr.sgo.bcm.flightaggregator.information.model.FlightRequest;
import fr.sgo.bcm.flightaggregator.information.model.TravelInformation;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class InfoResponse {
    private HttpStatus status;
    private FlightRequest request;
    private Map<Float, List<TravelInformation>> travelsByPrice;
}
